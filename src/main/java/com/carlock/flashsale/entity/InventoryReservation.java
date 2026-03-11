package com.carlock.flashsale.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Tracks inventory reservations. When a customer begins checkout,
 * stock is "reserved" here. If checkout isn't completed within
 * 10 minutes, the reservation expires and stock returns to the pool.
 *
 * NOTE: The current codebase does NOT use this entity properly.
 * The candidate must wire up the reservation/expiry lifecycle.
 */
@Entity
@Table(name = "inventory_reservations")
@NamedQueries({
    @NamedQuery(name = "InventoryReservation.findExpired",
                query = "SELECT r FROM InventoryReservation r WHERE r.status = 'HELD' AND r.expiresAt < :now"),
    @NamedQuery(name = "InventoryReservation.findByOrderRef",
                query = "SELECT r FROM InventoryReservation r WHERE r.orderReference = :orderReference")
})
public class InventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_reference", nullable = false, length = 64)
    private String orderReference;

    @Column(name = "product_sku", nullable = false, length = 40)
    private String productSku;

    @Column(name = "quantity_reserved", nullable = false)
    private int quantityReserved;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // HELD, CONFIRMED, EXPIRED

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reserved_at", nullable = false)
    private Date reservedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expires_at", nullable = false)
    private Date expiresAt;

    public InventoryReservation() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderReference() { return orderReference; }
    public void setOrderReference(String orderReference) { this.orderReference = orderReference; }

    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }

    public int getQuantityReserved() { return quantityReserved; }
    public void setQuantityReserved(int quantityReserved) { this.quantityReserved = quantityReserved; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getReservedAt() { return reservedAt; }
    public void setReservedAt(Date reservedAt) { this.reservedAt = reservedAt; }

    public Date getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Date expiresAt) { this.expiresAt = expiresAt; }
}
