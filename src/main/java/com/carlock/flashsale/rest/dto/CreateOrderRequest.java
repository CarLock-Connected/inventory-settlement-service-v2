package com.carlock.flashsale.rest.dto;

/**
 * DTO for the POST /api/orders request body.
 */
public class CreateOrderRequest {

    private String customerId;
    private String sku;
    private String variantCode;
    private int quantity;

    public CreateOrderRequest() {}

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getVariantCode() { return variantCode; }
    public void setVariantCode(String variantCode) { this.variantCode = variantCode; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
