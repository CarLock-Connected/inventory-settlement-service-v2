package com.carlock.flashsale.service;

import com.carlock.flashsale.entity.InventoryReservation;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Periodic cleanup worker that expires stale inventory reservations.
 *
 * CURRENT STATE: This is a skeleton. The candidate must complete
 * the implementation so that:
 *
 *   1. Every 60 seconds, find all reservations with status = 'HELD'
 *      where expires_at < NOW.
 *   2. For each expired reservation, restore the reserved quantity
 *      back to the product's quantity_on_hand.
 *   3. Update the reservation status to 'EXPIRED'.
 *   4. Handle failures gracefully — if restoring one reservation
 *      fails, the others should still be processed.
 *
 * IMPORTANT: The restore must be atomic. The candidate cannot use
 * the broken InventoryService.restoreInventory() method without
 * first fixing its race condition.
 *
 * BONUS: The candidate should consider what happens if two instances
 * of this cleanup run concurrently (clustered deployment). A
 * distributed lock or database-level row locking is needed.
 */
@Singleton
@Startup
public class ReservationCleanupService {

    private static final Logger LOG = Logger.getLogger(ReservationCleanupService.class.getName());

    @PersistenceContext(unitName = "flashSalePU")
    private EntityManager em;

    @Inject
    private InventoryService inventoryService;

    /**
     * Runs every 60 seconds. Currently logs a message and does nothing.
     * The candidate must implement the full cleanup logic.
     */
    @Schedule(second = "0", minute = "*", hour = "*", persistent = false)
    public void cleanupExpiredReservations() {
        LOG.info("Reservation cleanup triggered at " + new Date());

        // TODO: Implement the following:
        //
        // 1. Query for expired reservations:
        //    List<InventoryReservation> expired = em.createNamedQuery(
        //        "InventoryReservation.findExpired", InventoryReservation.class)
        //        .setParameter("now", new Date())
        //        .getResultList();
        //
        // 2. For each expired reservation:
        //    a. Restore inventory (using a FIXED, atomic method)
        //    b. Set reservation.status = "EXPIRED"
        //    c. Merge the reservation
        //
        // 3. Handle errors per-reservation (don't let one failure
        //    prevent others from being processed)

        LOG.info("Reservation cleanup completed (NO-OP — not yet implemented)");
    }
}
