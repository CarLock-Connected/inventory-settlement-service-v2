package com.carlock.flashsale.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "products")
@NamedQueries({
    @NamedQuery(name = "Product.findAll",
                query = "SELECT p FROM Product p"),
    @NamedQuery(name = "Product.findBySku",
                query = "SELECT p FROM Product p WHERE p.sku = :sku")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sku", nullable = false, unique = true, length = 40)
    private String sku;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "quantity_on_hand", nullable = false)
    private int quantityOnHand;

    @Column(name = "flash_sale_active", nullable = false)
    private boolean flashSaleActive;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ProductVariant> variants = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<ProductReview> reviews = new HashSet<>();

    @Version
    @Column(name = "version")
    private Long version;

    public Product() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public int getQuantityOnHand() { return quantityOnHand; }
    public void setQuantityOnHand(int quantityOnHand) { this.quantityOnHand = quantityOnHand; }

    public boolean isFlashSaleActive() { return flashSaleActive; }
    public void setFlashSaleActive(boolean flashSaleActive) { this.flashSaleActive = flashSaleActive; }

    public List<ProductVariant> getVariants() { return variants; }
    public void setVariants(List<ProductVariant> variants) { this.variants = variants; }

    public Set<ProductReview> getReviews() { return reviews; }
    public void setReviews(Set<ProductReview> reviews) { this.reviews = reviews; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
