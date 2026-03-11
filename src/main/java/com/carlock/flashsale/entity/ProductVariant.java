package com.carlock.flashsale.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_variants")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "variant_code", nullable = false, length = 20)
    private String variantCode;

    @Column(name = "color", length = 50)
    private String color;

    @Column(name = "size_label", length = 20)
    private String sizeLabel;

    @Column(name = "additional_stock", nullable = false)
    private int additionalStock;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "variant", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<VariantPriceTier> priceTiers = new ArrayList<>();

    public ProductVariant() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVariantCode() { return variantCode; }
    public void setVariantCode(String variantCode) { this.variantCode = variantCode; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getSizeLabel() { return sizeLabel; }
    public void setSizeLabel(String sizeLabel) { this.sizeLabel = sizeLabel; }

    public int getAdditionalStock() { return additionalStock; }
    public void setAdditionalStock(int additionalStock) { this.additionalStock = additionalStock; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public List<VariantPriceTier> getPriceTiers() { return priceTiers; }
    public void setPriceTiers(List<VariantPriceTier> priceTiers) { this.priceTiers = priceTiers; }
}
