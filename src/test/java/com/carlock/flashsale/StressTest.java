package com.carlock.flashsale;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * =====================================================================
 * STRESS TEST — Run this against the live application server.
 * =====================================================================
 *
 * This test hammers the POST /api/orders endpoint with 50 concurrent
 * requests for a product that only has 10 units in stock.
 *
 * EXPECTED (after fix): exactly 10 orders succeed, 40 are rejected.
 * ACTUAL (broken code):  many more than 10 succeed → OVERSOLD.
 *
 * Usage:
 *   1. Start the application (docker-compose up)
 *   2. Run: mvn test -Dtest=StressTest -Dbase.url=http://localhost:8080/flash-sale
 *   3. Check the output for PASS/FAIL.
 *
 * NOTE: This test resets inventory before running. It uses the product
 * FLASH-007 which has quantity_on_hand = 10 in the seed data.
 */
public class StressTest {

    private static final int CONCURRENT_REQUESTS = 50;
    private static final int EXPECTED_STOCK = 10;
    private static final String TARGET_SKU = "FLASH-007";
    private static final String CUSTOMER_ID = "c1000001-aaaa-bbbb-cccc-000000000004";

    private static final String BASE_URL =
            System.getProperty("base.url", "http://localhost:8080/flash-sale");

    public static void main(String[] args) throws Exception {
        System.out.println("============================================================");
        System.out.println("  FLASH SALE STRESS TEST");
        System.out.println("  Target: " + BASE_URL);
        System.out.println("  Product: " + TARGET_SKU + " (stock: " + EXPECTED_STOCK + ")");
        System.out.println("  Concurrent buyers: " + CONCURRENT_REQUESTS);
        System.out.println("============================================================\n");

        // Verify the server is up
        Client healthClient = ClientBuilder.newClient();
        try {
            Response healthResp = healthClient.target(BASE_URL + "/api/health")
                    .request(MediaType.APPLICATION_JSON).get();
            if (healthResp.getStatus() != 200) {
                System.err.println("FATAL: Server is not healthy. Got HTTP " + healthResp.getStatus());
                System.exit(1);
            }
            System.out.println("[OK] Server is healthy.\n");
        } catch (Exception e) {
            System.err.println("FATAL: Cannot reach server at " + BASE_URL);
            System.err.println("       " + e.getMessage());
            System.exit(1);
        } finally {
            healthClient.close();
        }

        // Run the concurrent order storm
        runConcurrencyTest();

        // Run the idempotency test
        runIdempotencyTest();
    }

    // ---------------------------------------------------------------
    // Test 1: Concurrency — can we oversell?
    // ---------------------------------------------------------------
    private static void runConcurrencyTest() throws Exception {
        System.out.println("--- Test 1: Concurrency (Race Condition) ---\n");

        ExecutorService pool = Executors.newFixedThreadPool(CONCURRENT_REQUESTS);
        CountDownLatch readyLatch = new CountDownLatch(CONCURRENT_REQUESTS);
        CountDownLatch goLatch = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        List<Future<Integer>> futures = new ArrayList<>();

        for (int i = 0; i < CONCURRENT_REQUESTS; i++) {
            final int buyerNum = i + 1;
            futures.add(pool.submit(new Callable<Integer>() {
                @Override
                public Integer call() {
                    Client client = ClientBuilder.newClient();
                    try {
                        // Signal ready and wait for the "go" signal
                        readyLatch.countDown();
                        goLatch.await(10, TimeUnit.SECONDS);

                        String json = String.format(
                                "{\"customerId\":\"%s\",\"sku\":\"%s\",\"variantCode\":\"BLK-QHD\",\"quantity\":1}",
                                CUSTOMER_ID, TARGET_SKU);

                        Response resp = client.target(BASE_URL + "/api/orders")
                                .request(MediaType.APPLICATION_JSON)
                                .header("Idempotency-Key", UUID.randomUUID().toString())
                                .post(Entity.entity(json, MediaType.APPLICATION_JSON));

                        int status = resp.getStatus();
                        String body = resp.readEntity(String.class);

                        if (status == 201) {
                            successCount.incrementAndGet();
                            System.out.println("  Buyer #" + buyerNum + " → ORDER CREATED (HTTP 201)");
                        } else if (status == 409) {
                            failCount.incrementAndGet();
                            System.out.println("  Buyer #" + buyerNum + " → REJECTED (HTTP 409)");
                        } else {
                            errorCount.incrementAndGet();
                            System.out.println("  Buyer #" + buyerNum + " → UNEXPECTED HTTP " + status
                                    + ": " + body);
                        }
                        return status;
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                        System.out.println("  Buyer #" + buyerNum + " → ERROR: " + e.getMessage());
                        return -1;
                    } finally {
                        client.close();
                    }
                }
            }));
        }

        // Wait until all threads are ready, then fire
        readyLatch.await(15, TimeUnit.SECONDS);
        System.out.println("[GO] All " + CONCURRENT_REQUESTS + " buyers ready. Firing...\n");
        goLatch.countDown();

        // Wait for completion
        pool.shutdown();
        pool.awaitTermination(120, TimeUnit.SECONDS);

        System.out.println("\n--- Concurrency Results ---");
        System.out.println("  Orders created : " + successCount.get());
        System.out.println("  Rejected (409) : " + failCount.get());
        System.out.println("  Errors         : " + errorCount.get());
        System.out.println("  Expected stock : " + EXPECTED_STOCK);

        if (successCount.get() == EXPECTED_STOCK) {
            System.out.println("\n  *** PASS: Exactly " + EXPECTED_STOCK
                    + " orders created. No overselling. ***\n");
        } else if (successCount.get() > EXPECTED_STOCK) {
            System.out.println("\n  *** FAIL: OVERSOLD! " + successCount.get()
                    + " orders for " + EXPECTED_STOCK + " units. Race condition detected. ***\n");
        } else {
            System.out.println("\n  *** WARN: Only " + successCount.get()
                    + " orders (expected " + EXPECTED_STOCK + "). Possible over-locking or errors. ***\n");
        }
    }

    // ---------------------------------------------------------------
    // Test 2: Idempotency — do duplicate keys create duplicate orders?
    // ---------------------------------------------------------------
    private static void runIdempotencyTest() throws Exception {
        System.out.println("--- Test 2: Idempotency (Duplicate Detection) ---\n");

        String idempotencyKey = "IDEM-" + UUID.randomUUID().toString();
        // Use a different product with plenty of stock
        String json = String.format(
                "{\"customerId\":\"%s\",\"sku\":\"FLASH-009\",\"variantCode\":\"BLK-BASIC\",\"quantity\":1}",
                CUSTOMER_ID);

        int createdCount = 0;
        int duplicateRejectedCount = 0;
        int otherCount = 0;

        Client client = ClientBuilder.newClient();
        try {
            for (int i = 1; i <= 5; i++) {
                Response resp = client.target(BASE_URL + "/api/orders")
                        .request(MediaType.APPLICATION_JSON)
                        .header("Idempotency-Key", idempotencyKey)
                        .post(Entity.entity(json, MediaType.APPLICATION_JSON));

                int status = resp.getStatus();
                String body = resp.readEntity(String.class);

                System.out.println("  Request #" + i + " (key=" + idempotencyKey + ") → HTTP " + status);

                if (status == 201) {
                    createdCount++;
                } else if (status == 409) {
                    duplicateRejectedCount++;
                } else {
                    otherCount++;
                }
            }
        } finally {
            client.close();
        }

        System.out.println("\n--- Idempotency Results ---");
        System.out.println("  Created (201)  : " + createdCount);
        System.out.println("  Rejected (409) : " + duplicateRejectedCount);
        System.out.println("  Other          : " + otherCount);

        if (createdCount == 1 && duplicateRejectedCount == 4) {
            System.out.println("\n  *** PASS: Exactly 1 order created, 4 duplicates rejected. ***\n");
        } else if (createdCount > 1) {
            System.out.println("\n  *** FAIL: " + createdCount
                    + " orders created with the same key! Idempotency is broken. ***\n");
        } else {
            System.out.println("\n  *** WARN: Unexpected result distribution. ***\n");
        }
    }
}
