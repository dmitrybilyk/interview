package com.conduct.interview._13_event_driven.spring_events.examples;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Three listener styles in one class.
 *
 * Each style answers a different question:
 *   @EventListener              → "Run immediately, same thread, same transaction."
 *   @Async @EventListener       → "Run in background thread, don't block the caller."
 *   @TransactionalEventListener → "Run only if the transaction commits — safe for side-effects."
 */
@Component
public class OrderEventListeners {

    // ── 1. Synchronous listener ──────────────────────────────────────────────
    // Runs in the same thread as the publisher.
    // If this throws → exception propagates to OrderService.placeOrder().
    // Use when: the side effect MUST complete before the method returns (e.g., validation).
    @EventListener
    public void onOrderPlaced(OrderEvents.OrderPlaced event) {
        System.out.println("[SYNC listener] OrderPlaced: " + event.orderId + " $" + event.amount);
        // e.g., send to inventory check, update cache
    }

    // Multiple listeners on the same event — all are called in registration order.
    @EventListener
    public void onOrderPlacedAudit(OrderEvents.OrderPlaced event) {
        System.out.println("[SYNC listener] Audit log: order " + event.orderId + " placed.");
    }

    // ── 2. Async listener ────────────────────────────────────────────────────
    // Runs in a separate thread (Spring's async executor).
    // Publisher returns immediately — listener runs concurrently.
    // If this throws → exception is logged, NOT propagated to caller.
    // Use when: the side effect is slow or non-critical (email, push notification).
    @Async
    @EventListener
    public void onOrderShipped(OrderEvents.OrderShipped event) throws InterruptedException {
        System.out.println("[ASYNC listener] thread=" + Thread.currentThread().getName()
                + " OrderShipped: " + event.orderId() + " tracking=" + event.trackingNumber());
        Thread.sleep(500);  // simulate slow email sending
        System.out.println("[ASYNC listener] Email sent for " + event.orderId());
    }

    // ── 3. Transactional listener ─────────────────────────────────────────────
    // Fired AFTER_COMMIT (default phase).
    // If the transaction rolls back → this listener is NEVER called.
    // This solves the dual-write problem: listener only runs if DB write succeeded.
    //
    // Important: this listener does NOT run inside a transaction by default.
    // If you need a new transaction here, add @Transactional(propagation = REQUIRES_NEW).
    //
    // Use when: publishing to an external broker/email/webhook after a DB change.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCancelled(OrderEvents.OrderCancelled event) {
        System.out.println("[TX listener] AFTER_COMMIT: order " + event.orderId()
                + " cancelled. Reason: " + event.reason());
        System.out.println("[TX listener] Safe to call external API now — DB is committed.");
    }

    // BEFORE_COMMIT phase — runs while the transaction is still open.
    // Throwing here ROLLS BACK the transaction.
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onOrderCancelledValidate(OrderEvents.OrderCancelled event) {
        System.out.println("[TX listener] BEFORE_COMMIT validation for order " + event.orderId());
        // if (invalidCancellation) throw new IllegalStateException("rollback!");
    }

    // AFTER_ROLLBACK — cleanup on failure (undo side effects if any were done before the rollback)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void onOrderCancelledRollback(OrderEvents.OrderCancelled event) {
        System.out.println("[TX listener] AFTER_ROLLBACK: compensate for order " + event.orderId());
    }
}
