package com.conduct.interview._13_event_driven.spring_events.examples;

import org.springframework.context.ApplicationEvent;

/**
 * Domain event classes. Extend ApplicationEvent (or use any plain object with @EventListener).
 * Spring 4.2+ allows plain POJOs as events — no need to extend ApplicationEvent.
 */
public class OrderEvents {

    // Classic style: extends ApplicationEvent
    public static class OrderPlaced extends ApplicationEvent {
        public final String orderId;
        public final double amount;

        public OrderPlaced(Object source, String orderId, double amount) {
            super(source);
            this.orderId = orderId;
            this.amount  = amount;
        }
    }

    // Modern style: plain POJO (works with @EventListener, @TransactionalEventListener)
    public record OrderShipped(String orderId, String trackingNumber) {}

    public record OrderCancelled(String orderId, String reason) {}
}
