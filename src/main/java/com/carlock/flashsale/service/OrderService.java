package com.carlock.flashsale.service;

import com.carlock.flashsale.entity.OrderLineItem;
import com.carlock.flashsale.entity.OrderStatus;
import com.carlock.flashsale.entity.Product;
import com.carlock.flashsale.entity.SaleOrder;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Stateless
public class OrderService {

    private static final Logger LOG = Logger.getLogger(OrderService.class.getName());

    /** Default product category sent to the external pricing/tax API. */
    private static final String DEFAULT_CATEGORY = "ELECTRONICS";
    /** Default shipping state for tax calculation. */
    private static final String DEFAULT_STATE = "NY";

    @PersistenceContext(unitName = "flashSalePU")
    private EntityManager em;

    @Inject
    private InventoryService inventoryService;

    @Inject
    private DiscountService discountService;

    @Inject
    private ExternalPricingClient pricingClient;

    public SaleOrder createOrder(
        String customerId,
        String sku,
        String variantCode,
        int quantity,
        String idempotencyKey
    ) {
        LOG.info("Creating order: customer=" + customerId + ", sku=" + sku
                + ", qty=" + quantity + ", idempotencyKey=" + idempotencyKey);

        Product product = inventoryService.getProductBySku(sku);

        if (product == null) {
            LOG.warning("Product not found: " + sku);
            return null;
        }

        if (!inventoryService.deductInventory(sku, quantity)) {
            LOG.warning("Could not deduct inventory for " + sku);
            return null;
        }

        BigDecimal unitPrice = discountService.calculateEffectivePrice(product, customerId);
        BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

        // BUG: This call hits the external pricing API on every order.
        // If the external service is slow or down, the entire order blocks.
        // There is no timeout, no circuit breaker, and no fallback wired in.
        BigDecimal taxRate = pricingClient.fetchTaxRate(DEFAULT_CATEGORY, DEFAULT_STATE);
        BigDecimal taxAmount = lineTotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalWithTax = lineTotal.add(taxAmount);

        LOG.info("Tax for order: subtotal=" + lineTotal + ", taxRate=" + taxRate
                + ", tax=" + taxAmount + ", total=" + totalWithTax);

        SaleOrder order = new SaleOrder();
        order.setOrderReference("ORD-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        order.setCustomerId(customerId);
        order.setStatus(OrderStatus.CONFIRMED); // BUG: skips PENDING and INVENTORY_RESERVED states
        order.setTotalAmount(totalWithTax);
        order.setDiscountApplied(
                product.getBasePrice().subtract(unitPrice).multiply(BigDecimal.valueOf(quantity)));
        order.setCreatedAt(new Date());
        order.setIdempotencyKey(idempotencyKey); // Stored but never enforced

        OrderLineItem item = new OrderLineItem();
        item.setOrder(order);
        item.setProductSku(sku);
        item.setVariantCode(variantCode);
        item.setQuantity(quantity);
        item.setUnitPrice(unitPrice);
        item.setLineTotal(lineTotal);
        order.getLineItems().add(item);

        em.persist(order);

        LOG.info("Order created: " + order.getOrderReference());
        return order;
    }

    public SaleOrder findByReference(String orderReference) {
        List<SaleOrder> results = em.createNamedQuery("SaleOrder.findByOrderRef", SaleOrder.class)
                .setParameter("orderReference", orderReference)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}
