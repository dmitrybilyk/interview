package com.conduct.interview._3_spring._7_events;

import org.springframework.context.ApplicationListener;

/** Interface style: always active, no annotation-config needed. */
public class AuditLogListener implements ApplicationListener<OrderPlacedEvent> {
    @Override
    public void onApplicationEvent(OrderPlacedEvent event) {
        System.out.println("AuditLogListener (interface style) -> order " + event.getOrderId() + " placed");
    }
}
