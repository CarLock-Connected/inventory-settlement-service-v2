package com.carlock.flashsale.service;

import com.carlock.flashsale.entity.Product;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.logging.Logger;

@Stateless
public class InventoryService {

    private static final Logger LOG = Logger.getLogger(InventoryService.class.getName());

    @PersistenceContext(unitName = "flashSalePU")
    private EntityManager em;

    public boolean deductInventory(String sku, int quantity) {
        List<Product> results = em.createNamedQuery("Product.findBySku", Product.class)
                .setParameter("sku", sku)
                .getResultList();

        if (results.isEmpty()) {
            LOG.warning("Product not found for SKU: " + sku);
            return false;
        }

        Product product = results.get(0);

        int currentStock = product.getQuantityOnHand();

        if (currentStock < quantity) {
            LOG.info("Insufficient stock for " + sku + ": have " + currentStock + ", need " + quantity);
            return false;
        }

        product.setQuantityOnHand(currentStock - quantity);
        em.merge(product);

        LOG.info("Deducted " + quantity + " from " + sku + ". New stock: " + (currentStock - quantity));
        return true;
    }

    public void restoreInventory(String sku, int quantity) {
        List<Product> results = em.createNamedQuery("Product.findBySku", Product.class)
                .setParameter("sku", sku)
                .getResultList();

        if (results.isEmpty()) {
            LOG.severe("Cannot restore inventory — product not found: " + sku);
            return;
        }

        Product product = results.get(0);
        product.setQuantityOnHand(product.getQuantityOnHand() + quantity);
        em.merge(product);

        LOG.info("Restored " + quantity + " units to " + sku);
    }

    /**
     * Returns all products with their variants and reviews.
     */
    public List<Product> getAllProducts() {
        return em.createNamedQuery("Product.findAll", Product.class).getResultList();
    }

    public Product getProductBySku(String sku) {
        List<Product> results = em.createNamedQuery("Product.findBySku", Product.class)
                .setParameter("sku", sku)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
}
