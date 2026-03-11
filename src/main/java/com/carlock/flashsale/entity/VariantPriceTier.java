package com.carlock.flashsale.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "variant_price_tiers")
public class VariantPriceTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "min_quantity", nullable = false)
    private int minQuantity;

    @Column(name = "tier_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal tierPrice;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;

    public VariantPriceTier() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getMinQuantity() { return minQuantity; }
    public void setMinQuantity(int minQuantity) { this.minQuantity = minQuantity; }

    public BigDecimal getTierPrice() { return tierPrice; }
    public void setTierPrice(BigDecimal tierPrice) { this.tierPrice = tierPrice; }

    public ProductVariant getVariant() { return variant; }
    public void setVariant(ProductVariant variant) { this.variant = variant; }
}
