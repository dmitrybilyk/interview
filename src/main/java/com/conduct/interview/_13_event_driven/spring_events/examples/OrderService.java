package com.conduct.interview._13_event_driven.spring_events.examples;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Publishes domain events via ApplicationEventPublisher.
 *
 * The publisher is completely decoupled from listeners — it doesn't
 * know what (or how many) listeners are registered.
 */
@Service
public class OrderService {

    private final ApplicationEventPublisher publisher;

    public OrderService(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    // ── Sync event ──────────────────────────────────────────────────────────
    // Listeners run in the SAME thread before this method returns.
    // If a listener throws → exception propagates here.
    public void placeOrder(String orderId, double amount) {
        System.out.println("[OrderService] Placing order " + orderId);
        publisher.publishEvent(new OrderEvents.OrderPlaced(this, orderId, amount));
        System.out.println("[OrderService] placeOrder() returned (listeners already ran)");
    }

    // ── Async event ──────────────────────────────────────────────────────────
    // Listener is annotated @Async → runs in a thread pool, this returns immediately.
    public void shipOrder(String orderId, String tracking) {
        System.out.println("[OrderService] Shipping order " + orderId);
        publisher.publishEvent(new OrderEvents.OrderShipped(orderId, tracking));
        System.out.println("[OrderService] shipOrder() returned (async listener still running)");
    }

    // ── Transactional event ───────────────────────────────────────────────────
    // Listener uses @TransactionalEventListener(phase = AFTER_COMMIT).
    // Event is held until the transaction commits — avoids the dual-write problem
    // (listener only runs if the DB write actually succeeded).
    @Transactional
    public void cancelOrder(String orderId, String reason) {
        System.out.println("[OrderService] Cancelling order " + orderId + " in transaction...");
        // Imagine: orderRepo.save(order.cancel())
        publisher.publishEvent(new OrderEvents.OrderCancelled(orderId, reason));
        System.out.println("[OrderService] cancelOrder() tx committing — listener fires AFTER commit");
    }
}
