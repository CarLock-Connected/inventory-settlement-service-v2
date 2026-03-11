package com.carlock.flashsale.service;

import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * =====================================================================
 * THE "SLOW NEIGHBOR" PROBLEM
 * =====================================================================
 *
 * This service calls an external pricing/tax API to get real-time
 * tax rates. The external service is unreliable:
 *   - Average response time: 200ms
 *   - P99 response time: 8 seconds
 *   - Failure rate: ~5% (returns HTTP 500 or times out)
 *
 * The current implementation has NO circuit breaker, NO timeout
 * configuration, and NO fallback. When the external service is slow,
 * it blocks the calling thread, exhausting the EJB thread pool and
 * causing cascading failures across the entire application.
 *
 * The candidate must implement:
 *   1. A circuit breaker pattern (open after N failures, half-open
 *      after a cooldown period)
 *   2. A sensible timeout (e.g., 2 seconds)
 *   3. A fallback strategy (e.g., use a cached/default tax rate)
 *
 * CONSTRAINT: You may NOT add MicroProfile Fault Tolerance or any
 * third-party circuit breaker library. Implement it manually using
 * Java EE primitives (EJB timers, CDI interceptors, or plain Java).
 */
@Stateless
public class ExternalPricingClient {
    private static final Logger LOG = Logger.getLogger(ExternalPricingClient.class.getName());

    /**
     * The external pricing service URL.
     * In production this comes from a JNDI resource or system property.
     */
    private static final String PRICING_SERVICE_URL =
            System.getProperty("pricing.service.url", "http://pricing-service.internal:8080/api/v1/tax-rate");

    /**
     * BUG: No timeout configured. The JAX-RS client will wait
     * indefinitely for a response. If the external service hangs,
     * this thread is stuck forever.
     */
    private final Client httpClient = ClientBuilder.newClient();

    /**
     * Fetches the current tax rate for a given product category and
     * shipping state.
     *
     * BUG: No circuit breaker. Even if the last 50 calls failed,
     * this method will still attempt to call the external service,
     * wasting threads and adding latency.
     *
     * BUG: No fallback. If the call fails, the order creation fails
     * entirely, even though a reasonable default tax rate (e.g., 8.25%)
     * could be used.
     *
     * @param productCategory The product category code
     * @param shippingState   The US state abbreviation
     * @return The tax rate as a decimal (e.g., 0.0825 for 8.25%)
     */
    public BigDecimal fetchTaxRate(String productCategory, String shippingState) {
        String url = PRICING_SERVICE_URL + "?category=" + productCategory
                + "&state=" + shippingState;

        LOG.info("Fetching tax rate from external service: " + url);

        try {
            Response response = httpClient.target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get();

            if (response.getStatus() == 200) {
                String body = response.readEntity(String.class);
                // Expects response like: {"taxRate": 0.0825}
                // Naive parsing — another thing the candidate might notice
                String rateStr = body.replaceAll(".*\"taxRate\"\\s*:\\s*", "")
                        .replaceAll("[^0-9.].*", "");
                return new BigDecimal(rateStr);
            } else {
                LOG.warning("External pricing service returned HTTP " + response.getStatus());
                // BUG: No fallback — just throws, failing the entire order
                throw new RuntimeException("External pricing service error: HTTP " + response.getStatus());
            }
        } catch (RuntimeException e) {
            LOG.log(Level.SEVERE, "Failed to fetch tax rate", e);
            // BUG: Exception propagates up, killing the order transaction
            throw e;
        }
    }

    /**
     * This method exists but is NEVER CALLED. It represents the
     * fallback that should be used when the external service is down.
     * The candidate should wire this into a circuit breaker pattern.
     */
    public BigDecimal getDefaultTaxRate(String shippingState) {
        // Default rates by state — a reasonable fallback
        switch (shippingState) {
            case "CA": return new BigDecimal("0.0725");
            case "TX": return new BigDecimal("0.0625");
            case "NY": return new BigDecimal("0.0800");
            case "FL": return new BigDecimal("0.0600");
            default:   return new BigDecimal("0.0825"); // National average
        }
    }
}
