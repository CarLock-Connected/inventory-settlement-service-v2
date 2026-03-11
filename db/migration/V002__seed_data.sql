-- =====================================================================
-- Seed data for the Flash Sale challenge.
-- Generates enough data to make the N+1 problem observable. (MySQL 8.0)
-- =====================================================================

-- 10 Products (some with flash_sale_active = true)
INSERT INTO products (sku, name, base_price, quantity_on_hand, flash_sale_active, version) VALUES
('FLASH-001', 'Wireless Noise-Cancelling Headphones', 299.99, 50, TRUE, 0),
('FLASH-002', 'Ultra-Slim Laptop Stand',              89.99,  120, TRUE, 0),
('FLASH-003', 'Mechanical Keyboard RGB',              149.99, 30, TRUE, 0),
('FLASH-004', '4K Webcam Pro',                        199.99, 15, TRUE, 0),
('FLASH-005', 'USB-C Hub 12-in-1',                    79.99,  200, FALSE, 0),
('FLASH-006', 'Ergonomic Mouse Wireless',             69.99,  180, TRUE, 0),
('FLASH-007', '27" Monitor 165Hz',                    449.99, 10, TRUE, 0),
('FLASH-008', 'Standing Desk Electric',               599.99, 8, FALSE, 0),
('FLASH-009', 'Cable Management Kit',                 24.99,  500, FALSE, 0),
('FLASH-010', 'Desk Mat XXL',                         39.99,  300, TRUE, 0);

-- 5 variants per product = 50 variants total
-- Product 1 variants
INSERT INTO product_variants (product_id, variant_code, color, size_label, additional_stock) VALUES
(1, 'BLK-STD', 'Black', 'Standard', 10),
(1, 'WHT-STD', 'White', 'Standard', 8),
(1, 'BLK-PRO', 'Black', 'Pro', 5),
(1, 'SLV-STD', 'Silver', 'Standard', 12),
(1, 'RED-LTD', 'Red', 'Limited', 3);

-- Product 2 variants
INSERT INTO product_variants (product_id, variant_code, color, size_label, additional_stock) VALUES
(2, 'GRY-SM', 'Grey', 'Small', 20),
(2, 'GRY-LG', 'Grey', 'Large', 25),
(2, 'BLK-SM', 'Black', 'Small', 15),
(2, 'BLK-LG', 'Black', 'Large', 30),
(2, 'WHT-LG', 'White', 'Large', 10);

-- Product 3 variants
INSERT INTO product_variants (product_id, variant_code, color, size_label, additional_stock) VALUES
(3, 'BLU-US', 'Blue', 'US Layout', 6),
(3, 'BLK-US', 'Black', 'US Layout', 8),
(3, 'WHT-US', 'White', 'US Layout', 4),
(3, 'BLK-UK', 'Black', 'UK Layout', 5),
(3, 'PNK-US', 'Pink', 'US Layout', 2);

-- Product 4 variants
INSERT INTO product_variants (product_id, variant_code, color, size_label, additional_stock) VALUES
(4, 'BLK-1080', 'Black', '1080p', 10),
(4, 'BLK-4K', 'Black', '4K', 5),
(4, 'WHT-1080', 'White', '1080p', 8),
(4, 'WHT-4K', 'White', '4K', 3),
(4, 'GRY-4K', 'Grey', '4K', 2);

-- Product 5 variants
INSERT INTO product_variants (product_id, variant_code, color, size_label, additional_stock) VALUES
(5, 'SLV-A', 'Silver', 'Type A', 40),
(5, 'SLV-C', 'Silver', 'Type C', 50),
(5, 'GRY-A', 'Grey', 'Type A', 30),
(5, 'GRY-C', 'Grey', 'Type C', 45),
(5, 'BLK-C', 'Black', 'Type C', 35);

-- Product 6 variants
INSERT INTO product_variants (product_id, variant_code, color, size_label, additional_stock) VALUES
(6, 'BLK-SM', 'Black', 'Small', 30),
(6, 'BLK-LG', 'Black', 'Large', 40),
(6, 'WHT-SM', 'White', 'Small', 25),
(6, 'WHT-LG', 'White', 'Large', 35),
(6, 'PNK-SM', 'Pink', 'Small', 15);

-- Product 7 variants
INSERT INTO product_variants (product_id, variant_code, color, size_label, additional_stock) VALUES
(7, 'BLK-FHD', 'Black', 'FHD', 4),
(7, 'BLK-QHD', 'Black', 'QHD', 3),
(7, 'SLV-FHD', 'Silver', 'FHD', 2),
(7, 'SLV-QHD', 'Silver', 'QHD', 1),
(7, 'WHT-QHD', 'White', 'QHD', 2);

-- Product 8 variants
INSERT INTO product_variants (product_id, variant_code, color, size_label, additional_stock) VALUES
(8, 'BLK-48', 'Black', '48 inch', 3),
(8, 'BLK-60', 'Black', '60 inch', 2),
(8, 'WHT-48', 'White', '48 inch', 4),
(8, 'WHT-60', 'White', '60 inch', 1),
(8, 'WAL-60', 'Walnut', '60 inch', 2);

-- Product 9 variants
INSERT INTO product_variants (product_id, variant_code, color, size_label, additional_stock) VALUES
(9, 'BLK-BASIC', 'Black', 'Basic', 100),
(9, 'BLK-PRO', 'Black', 'Pro', 80),
(9, 'WHT-BASIC', 'White', 'Basic', 90),
(9, 'WHT-PRO', 'White', 'Pro', 70),
(9, 'GRY-PRO', 'Grey', 'Pro', 60);

-- Product 10 variants
INSERT INTO product_variants (product_id, variant_code, color, size_label, additional_stock) VALUES
(10, 'BLK-STD', 'Black', 'Standard', 60),
(10, 'GRY-STD', 'Grey', 'Standard', 50),
(10, 'BLU-STD', 'Blue', 'Standard', 40),
(10, 'GRN-STD', 'Green', 'Standard', 30),
(10, 'PNK-STD', 'Pink', 'Standard', 20);

-- 3 price tiers per variant = 150 price tier rows total
-- (Generating for first 10 variants; pattern repeats)
INSERT INTO variant_price_tiers (variant_id, min_quantity, tier_price) VALUES
(1, 1, 299.99), (1, 5, 279.99), (1, 10, 259.99),
(2, 1, 299.99), (2, 5, 279.99), (2, 10, 259.99),
(3, 1, 329.99), (3, 5, 309.99), (3, 10, 289.99),
(4, 1, 299.99), (4, 5, 279.99), (4, 10, 259.99),
(5, 1, 349.99), (5, 5, 329.99), (5, 10, 299.99),
(6, 1, 89.99),  (6, 5, 79.99),  (6, 10, 69.99),
(7, 1, 99.99),  (7, 5, 89.99),  (7, 10, 79.99),
(8, 1, 89.99),  (8, 5, 79.99),  (8, 10, 69.99),
(9, 1, 99.99),  (9, 5, 89.99),  (9, 10, 79.99),
(10, 1, 89.99), (10, 5, 79.99), (10, 10, 69.99),
(11, 1, 149.99),(11, 5, 139.99),(11, 10, 129.99),
(12, 1, 149.99),(12, 5, 139.99),(12, 10, 129.99),
(13, 1, 149.99),(13, 5, 139.99),(13, 10, 129.99),
(14, 1, 159.99),(14, 5, 149.99),(14, 10, 139.99),
(15, 1, 169.99),(15, 5, 159.99),(15, 10, 149.99),
(16, 1, 179.99),(16, 5, 169.99),(16, 10, 159.99),
(17, 1, 199.99),(17, 5, 189.99),(17, 10, 179.99),
(18, 1, 179.99),(18, 5, 169.99),(18, 10, 159.99),
(19, 1, 199.99),(19, 5, 189.99),(19, 10, 179.99),
(20, 1, 189.99),(20, 5, 179.99),(20, 10, 169.99),
(21, 1, 79.99), (21, 5, 69.99), (21, 10, 59.99),
(22, 1, 79.99), (22, 5, 69.99), (22, 10, 59.99),
(23, 1, 79.99), (23, 5, 69.99), (23, 10, 59.99),
(24, 1, 79.99), (24, 5, 69.99), (24, 10, 59.99),
(25, 1, 79.99), (25, 5, 69.99), (25, 10, 59.99),
(26, 1, 69.99), (26, 5, 59.99), (26, 10, 49.99),
(27, 1, 69.99), (27, 5, 59.99), (27, 10, 49.99),
(28, 1, 69.99), (28, 5, 59.99), (28, 10, 49.99),
(29, 1, 69.99), (29, 5, 59.99), (29, 10, 49.99),
(30, 1, 79.99), (30, 5, 69.99), (30, 10, 59.99),
(31, 1, 449.99),(31, 5, 429.99),(31, 10, 399.99),
(32, 1, 499.99),(32, 5, 479.99),(32, 10, 449.99),
(33, 1, 449.99),(33, 5, 429.99),(33, 10, 399.99),
(34, 1, 499.99),(34, 5, 479.99),(34, 10, 449.99),
(35, 1, 519.99),(35, 5, 499.99),(35, 10, 469.99),
(36, 1, 549.99),(36, 5, 529.99),(36, 10, 499.99),
(37, 1, 599.99),(37, 5, 579.99),(37, 10, 549.99),
(38, 1, 549.99),(38, 5, 529.99),(38, 10, 499.99),
(39, 1, 599.99),(39, 5, 579.99),(39, 10, 549.99),
(40, 1, 649.99),(40, 5, 629.99),(40, 10, 599.99),
(41, 1, 24.99), (41, 5, 22.99), (41, 10, 19.99),
(42, 1, 34.99), (42, 5, 32.99), (42, 10, 29.99),
(43, 1, 24.99), (43, 5, 22.99), (43, 10, 19.99),
(44, 1, 34.99), (44, 5, 32.99), (44, 10, 29.99),
(45, 1, 34.99), (45, 5, 32.99), (45, 10, 29.99),
(46, 1, 39.99), (46, 5, 37.99), (46, 10, 34.99),
(47, 1, 39.99), (47, 5, 37.99), (47, 10, 34.99),
(48, 1, 39.99), (48, 5, 37.99), (48, 10, 34.99),
(49, 1, 39.99), (49, 5, 37.99), (49, 10, 34.99),
(50, 1, 44.99), (50, 5, 42.99), (50, 10, 39.99);

-- Reviews (3-5 per product = ~40 rows)
INSERT INTO product_reviews (product_id, reviewer_name, rating, comment, created_at) VALUES
(1, 'AudioPhile88', 5, 'Best noise cancellation I have ever used. Worth every penny.', '2025-01-15 10:30:00'),
(1, 'TechReviewer', 4, 'Great sound, but the ear cups get warm after 2 hours.', '2025-02-20 14:15:00'),
(1, 'CasualListener', 3, 'Good for the price but nothing special about the bass.', '2025-03-10 09:00:00'),
(1, 'FreqFlyer', 5, 'These are my go-to for long flights. Amazing ANC.', '2025-04-05 16:45:00'),
(2, 'DeskSetupPro', 5, 'Sleek and sturdy. My MacBook Pro fits perfectly.', '2025-01-20 11:00:00'),
(2, 'Minimalist', 4, 'Clean design but wish it had more tilt angles.', '2025-02-28 13:30:00'),
(2, 'WFHWarrior', 4, 'Improved my posture significantly.', '2025-03-15 08:45:00'),
(3, 'TypeRacer', 5, 'Cherry MX switches + RGB = perfection.', '2025-01-10 12:00:00'),
(3, 'Coder42', 4, 'A bit loud for office use, but amazing feel.', '2025-02-14 15:00:00'),
(3, 'GamerDude', 5, 'Low latency and customizable per-key lighting.', '2025-03-22 20:00:00'),
(3, 'NightOwl', 3, 'RGB is great but the software is buggy on Linux.', '2025-04-01 23:30:00'),
(4, 'StreamerPro', 5, 'Crisp 4K image. Best webcam under $200.', '2025-02-01 10:00:00'),
(4, 'RemoteWorker', 4, 'Great camera but the mic picks up background noise.', '2025-03-05 14:30:00'),
(4, 'ContentCreator', 4, 'Good auto-exposure handling in mixed lighting.', '2025-04-10 11:15:00'),
(5, 'TechNomad', 5, 'All the ports I need in one small hub.', '2025-01-25 09:00:00'),
(5, 'Traveler99', 4, 'Gets warm under heavy use but works great.', '2025-02-18 16:00:00'),
(5, 'DevOpsGuy', 3, 'HDMI output occasionally flickers on 4K.', '2025-03-30 12:45:00'),
(6, 'ErgoFan', 5, 'Finally a mouse that does not hurt my wrist.', '2025-01-30 08:00:00'),
(6, 'ClickMaster', 4, 'Smooth scroll, good weight distribution.', '2025-02-22 13:00:00'),
(6, 'Designer01', 4, 'Great for long Figma sessions.', '2025-03-18 17:30:00'),
(7, 'PixelPerfect', 5, '165Hz at this price is unbeatable.', '2025-01-12 11:30:00'),
(7, 'GamerElite', 5, 'No ghosting, vibrant colors. IPS panel is top tier.', '2025-02-25 19:00:00'),
(7, 'CasualUser', 3, 'Overkill for my use but the display is gorgeous.', '2025-04-02 10:00:00'),
(8, 'StandingDeskFan', 5, 'Smooth motor, quiet operation. Love the memory presets.', '2025-01-18 14:00:00'),
(8, 'BackPainGone', 4, 'Helped my back pain but assembly took 2 hours.', '2025-03-08 09:30:00'),
(9, 'NeatFreak', 5, 'My desk went from chaos to clean in 30 minutes.', '2025-02-10 12:00:00'),
(9, 'Organizer', 4, 'Good value kit. Velcro ties could be longer.', '2025-03-25 15:00:00'),
(9, 'CableHater', 5, 'Should have bought this years ago.', '2025-04-08 08:00:00'),
(10, 'DeskAesthetic', 5, 'Huge mat, smooth surface, great for both keyboard and mouse.', '2025-01-22 10:00:00'),
(10, 'MousePadKing', 4, 'Edges started curling after a month. Otherwise solid.', '2025-02-15 14:00:00'),
(10, 'Minimalist', 4, 'Clean look, comfortable surface.', '2025-03-20 11:30:00');

-- Customer accounts (mix of old and new accounts for discount testing)
INSERT INTO customer_accounts (customer_id, email, display_name, account_created_at, loyalty_tier, is_active) VALUES
('c1000001-aaaa-bbbb-cccc-000000000001', 'alice@example.com', 'Alice', '2023-06-15 10:00:00', 'GOLD', TRUE),
('c1000001-aaaa-bbbb-cccc-000000000002', 'bob@example.com', 'Bob', '2024-01-20 14:30:00', 'SILVER', TRUE),
('c1000001-aaaa-bbbb-cccc-000000000003', 'charlie@example.com', 'Charlie', '2025-11-01 09:00:00', 'BRONZE', TRUE),
('c1000001-aaaa-bbbb-cccc-000000000004', 'diana@example.com', 'Diana', '2022-03-10 16:00:00', 'PLATINUM', TRUE),
('c1000001-aaaa-bbbb-cccc-000000000005', 'eve@example.com', 'Eve', '2026-02-28 12:00:00', NULL, TRUE),
('c1000001-aaaa-bbbb-cccc-000000000006', 'frank@example.com', 'Frank', '2024-08-15 08:00:00', 'GOLD', FALSE);

-- Alice: account > 1 year old (created June 2023) → eligible for discount
-- Bob:   account > 1 year old (created Jan 2024)  → eligible for discount
-- Charlie: account < 1 year old (created Nov 2025) → NOT eligible
-- Diana: account > 1 year old (created Mar 2022)  → eligible for discount
-- Eve:   brand new account (created Feb 2026)      → NOT eligible
-- Frank: account > 1 year old but INACTIVE          → eligible by age but inactive
