package com.carlock.flashsale.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "customer_accounts")
@NamedQueries({
    @NamedQuery(name = "CustomerAccount.findByCustomerId",
                query = "SELECT c FROM CustomerAccount c WHERE c.customerId = :customerId")
})
public class CustomerAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false, unique = true, length = 36)
    private String customerId;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "display_name", length = 100)
    private String displayName;

    /**
     * The date this account was created. Used for discount eligibility.
     * Business rule: account must be older than 1 year to qualify for
     * flash-sale discounts.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "account_created_at", nullable = false)
    private Date accountCreatedAt;

    @Column(name = "loyalty_tier", length = 20)
    private String loyaltyTier;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    public CustomerAccount() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public Date getAccountCreatedAt() { return accountCreatedAt; }
    public void setAccountCreatedAt(Date accountCreatedAt) { this.accountCreatedAt = accountCreatedAt; }

    public String getLoyaltyTier() { return loyaltyTier; }
    public void setLoyaltyTier(String loyaltyTier) { this.loyaltyTier = loyaltyTier; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
