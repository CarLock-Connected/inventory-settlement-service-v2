# Project: Inventory Settlement Service v2 (Legacy Refactor)

## 1. Scenario
You have been tasked with stabilizing the **Flash Sale Inventory Service**. During a recent high-traffic event, the service suffered from "Inventory Leakage" (overselling), severe latency spikes, and thread pool exhaustion.

The previous team attempted a fix but failed. You are required to perform a **Forensic Audit** and implement a resilient, thread-safe architecture.

> **Important:** A full rewrite is not an option. You must work within the existing Java EE 7 monolith and adhere to the strict technical constraints provided.

---

## 2. Technical Stack & Constraints

| Component      | Specification                                              |
|:---------------|:-----------------------------------------------------------|
| **Language**   | Java 8 (No Java 9+ features allowed)                       |
| **Platform**   | Java EE 7 (EJB 3.2, CDI 1.2, JPA 2.1, JAX-RS 2.0)          |
| **Database**   | MySQL 8.0 (Schema is **immutable**; no migrations allowed) |
| **Cache/Lock** | Redis 4.x (Jedis client provided)                          |
| **Server**     | WildFly 10                                                 |

---

## 3. Part I: The Forensic Audit
Before writing code, investigate the provided project. You must submit a `DIAGNOSTIC.md` file identifying **at least 5 critical architectural flaws**. For each flaw, document:
1. **The Symptom:** What happens during the `StressTest.java` execution?
2. **The Root Cause:** Point to the specific class/method (e.g., race conditions, N+1 patterns, or timezone mishandling).
3. **The Impact:** How does this affect the business (e.g., lost revenue, database exhaustion, or incorrect billing)?

---

## 4. Part II: Stabilization Requirements

### 4.1 Atomic Inventory Settlement
The current `deductInventory` logic allows the system to sell more items than are physically available under high concurrency.
* **Objective:** Ensure inventory deductions are atomic and survive a clustered environment.
* **Constraint:** Do **not** use `synchronized` blocks or `ReentrantLock`. The solution must scale across multiple application server nodes.

### 4.2 Data Access Optimization
The `GET /api/products` endpoint is currently "DDoS-ing" the database. Loading the catalog generates a "Query Flood" that exhausts the connection pool.
* **Objective:** Refactor the JPA fetching strategy so that retrieving a Product with its Variants, Price Tiers, and Reviews uses a **fixed number of queries** ($\le 4$), regardless of the number of products.
* **Constraint:** You may not use a global 2nd-level cache.

### 4.3 Distributed Idempotency (Claim Check)
Duplicate `POST /api/orders` requests are creating duplicate charges and inventory deductions.
* **Objective:** Implement an idempotency filter using the provided Redis client. If a request with a specific `X-Idempotency-Key` is already in flight or has finished, subsequent requests must return the original response.
* **Constraint:** You may **not** add a unique constraint to the database. The enforcement must happen at the application/cache layer.

### 4.4 Time-Aware Discount Logic
Users in the Eastern Time Zone (New York) have reported that "Tuesday Discounts" are being applied prematurely on Monday evenings.
* **Objective:** Fix the logic in `DiscountService` to ensure the "Business Day" is calculated correctly according to the **America/New_York** timezone, specifically accounting for Daylight Savings (DST) transitions.

### 4.5 Manual Circuit Breaker
The system integrates with a legacy `ExternalPricingAPI` that frequently hangs for 30+ seconds.
* **Objective:** Implement a manual Circuit Breaker. If the external API fails or times out 5 times consecutively, trip the circuit to `OPEN` for 30 seconds and return a hardcoded fallback tax rate.
* **Constraint:** Do **not** use third-party libraries (Resilience4j, Hystrix, etc.). Implement this using standard Java/Java EE concurrency patterns.



---

## 5. Evaluation Criteria

| Criterion       | Weight | Success Metric                                                    |
|:----------------|:-------|:------------------------------------------------------------------|
| **Audit Depth** | 30%    | Identified non-obvious bugs (e.g. Cartesian Product, DST issues). |
| **Concurrency** | 30%    | `StressTest.java` shows 0% overselling with high contention.      |
| **Performance** | 20%    | Total DB queries for catalog load is $\le 4$.                     |
| **Resilience**  | 20%    | Circuit Breaker state transitions are thread-safe and correct.    |

---

## 6. Submission Instructions
1. Ensure `mvn clean verify` passes.
2. Include your `DIAGNOSTIC.md` audit.
3. Include a `README_FIXES.md` explaining why you chose your specific locking and idempotency strategies