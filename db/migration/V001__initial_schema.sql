-- =====================================================================
-- Flash Sale Inventory Service — Database Schema
-- MySQL 8.0+
-- =====================================================================
-- CONSTRAINT: Candidates may NOT modify this schema. All solutions
-- must work with these exact tables and columns.
-- =====================================================================

CREATE TABLE products (
    id              BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sku             VARCHAR(40)     NOT NULL UNIQUE,
    name            VARCHAR(200)    NOT NULL,
    base_price      DECIMAL(12,2)   NOT NULL,
    quantity_on_hand INT            NOT NULL DEFAULT 0,
    flash_sale_active TINYINT(1)    NOT NULL DEFAULT 0,
    version         BIGINT          NOT NULL DEFAULT 0
) ENGINE=InnoDB;

CREATE TABLE product_variants (
    id              BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    product_id      BIGINT          NOT NULL,
    variant_code    VARCHAR(20)     NOT NULL,
    color           VARCHAR(50),
    size_label      VARCHAR(20),
    additional_stock INT            NOT NULL DEFAULT 0,
    CONSTRAINT fk_variants_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB;

CREATE TABLE variant_price_tiers (
    id              BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    variant_id      BIGINT          NOT NULL,
    min_quantity    INT             NOT NULL,
    tier_price      DECIMAL(12,2)   NOT NULL,
    CONSTRAINT fk_tiers_variant FOREIGN KEY (variant_id) REFERENCES product_variants(id)
) ENGINE=InnoDB;

CREATE TABLE product_reviews (
    id              BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    product_id      BIGINT          NOT NULL,
    reviewer_name   VARCHAR(100),
    rating          INT             NOT NULL,
    comment         VARCHAR(2000),
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT fk_reviews_product FOREIGN KEY (product_id) REFERENCES products(id)
) ENGINE=InnoDB;

CREATE TABLE customer_accounts (
    id              BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    customer_id     VARCHAR(36)     NOT NULL UNIQUE,
    email           VARCHAR(255)    NOT NULL,
    display_name    VARCHAR(100),
    account_created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    loyalty_tier    VARCHAR(20),
    is_active       TINYINT(1)      NOT NULL DEFAULT 1
) ENGINE=InnoDB;

CREATE TABLE sale_orders (
    id              BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_reference VARCHAR(64)     NOT NULL UNIQUE,
    customer_id     VARCHAR(36)     NOT NULL,
    status          VARCHAR(20)     NOT NULL,
    total_amount    DECIMAL(14,2),
    discount_applied DECIMAL(14,2),
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NULL DEFAULT NULL,
    idempotency_key VARCHAR(64)
) ENGINE=InnoDB;

CREATE TABLE order_line_items (
    id              BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_id        BIGINT          NOT NULL,
    product_sku     VARCHAR(40)     NOT NULL,
    variant_code    VARCHAR(20),
    quantity        INT             NOT NULL,
    unit_price      DECIMAL(12,2)   NOT NULL,
    line_total      DECIMAL(14,2)   NOT NULL,
    CONSTRAINT fk_lineitems_order FOREIGN KEY (order_id) REFERENCES sale_orders(id)
) ENGINE=InnoDB;

CREATE TABLE inventory_reservations (
    id              BIGINT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
    order_reference VARCHAR(64)     NOT NULL,
    product_sku     VARCHAR(40)     NOT NULL,
    quantity_reserved INT           NOT NULL,
    status          VARCHAR(20)     NOT NULL DEFAULT 'HELD',
    reserved_at     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Indexes for common query patterns
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_variants_product ON product_variants(product_id);
CREATE INDEX idx_tiers_variant ON variant_price_tiers(variant_id);
CREATE INDEX idx_reviews_product ON product_reviews(product_id);
CREATE INDEX idx_orders_reference ON sale_orders(order_reference);
CREATE INDEX idx_orders_customer ON sale_orders(customer_id);
CREATE INDEX idx_reservations_status_expires ON inventory_reservations(status, expires_at);
CREATE INDEX idx_reservations_order ON inventory_reservations(order_reference);
