package com.carlock.flashsale.service;

import com.carlock.flashsale.entity.CustomerAccount;
import com.carlock.flashsale.entity.Product;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

/**
 * =====================================================================
 * CONTEXT-SPECIFIC BUSINESS RULE (the "AI trap"):
 * =====================================================================
 *
 * Flash-sale discounts (15% off) apply ONLY when ALL of the following
 * conditions are true:
 *
 *   1. The product has flash_sale_active = true
 *   2. The customer's account is older than 1 year (account_created_at
 *      is more than 365 days ago, measured at the current instant)
 *   3. The current day-of-week is TUESDAY in the America/New_York
 *      timezone (UTC-5, or UTC-4 during daylight saving time)
 *
 * IMPORTANT: The timezone is America/New_York (US Eastern), which
 * observes daylight saving time. "UTC-5" is a simplification used
 * in the business requirements document, but the implementation MUST
 * use America/New_York to be correct year-round.
 */
@Stateless
public class DiscountService {

    private static final Logger LOG = Logger.getLogger(DiscountService.class.getName());

    private static final BigDecimal FLASH_DISCOUNT_RATE = new BigDecimal("0.15");
    private static final int MINIMUM_ACCOUNT_AGE_DAYS = 365;

    @PersistenceContext(unitName = "flashSalePU")
    private EntityManager em;

    /**
     * Calculates the effective price for a product, applying the
     * flash-sale discount if all business rules are satisfied.
     *
     * @param product    The product being purchased
     * @param customerId The UUID of the purchasing customer
     * @return The effective unit price (discounted or base)
     */
    public BigDecimal calculateEffectivePrice(Product product, String customerId) {
        if (!product.isFlashSaleActive()) {
            LOG.fine("Product " + product.getSku() + " not in flash sale.");
            return product.getBasePrice();
        }

        CustomerAccount account = findCustomerAccount(customerId);

        if (account == null) {
            LOG.warning("Customer not found: " + customerId + ". No discount.");
            return product.getBasePrice();
        }

        if (!isAccountOldEnough(account)) {
            LOG.fine("Account " + customerId + " too new for discount.");
            return product.getBasePrice();
        }

        if (!isTuesdayInEastern()) {
            LOG.fine("Not Tuesday in Eastern time. No discount.");
            return product.getBasePrice();
        }

        // All conditions met — apply 15% discount
        BigDecimal discount = product.getBasePrice().multiply(FLASH_DISCOUNT_RATE);
        BigDecimal discountedPrice = product.getBasePrice().subtract(discount)
                .setScale(2, RoundingMode.HALF_UP);

        LOG.info("Flash discount applied for " + customerId + " on " + product.getSku()
                + ": " + product.getBasePrice() + " -> " + discountedPrice);

        return discountedPrice;
    }

    private CustomerAccount findCustomerAccount(String customerId) {
        List<CustomerAccount> results = em.createNamedQuery(
                "CustomerAccount.findByCustomerId", CustomerAccount.class)
                .setParameter("customerId", customerId)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Checks if the account was created more than 365 days ago.
     */
    private boolean isAccountOldEnough(CustomerAccount account) {
        Calendar cutoff = Calendar.getInstance();
        cutoff.add(Calendar.DAY_OF_YEAR, -MINIMUM_ACCOUNT_AGE_DAYS);
        return account.getAccountCreatedAt().before(cutoff.getTime());
    }

    private boolean isTuesdayInEastern() {
        // BUG: Uses system default timezone, not America/New_York
        Calendar now = Calendar.getInstance();

        // BUG HINT: Calendar.TUESDAY == 3 in Java.
        // The developer who wrote this tested on a machine set to
        // Eastern time — it worked there but fails on UTC servers.
        int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);

        LOG.fine("Current day of week (system TZ): " + dayOfWeek
                + " (TUESDAY=" + Calendar.TUESDAY + ")");

        return dayOfWeek == Calendar.TUESDAY;
    }
}
