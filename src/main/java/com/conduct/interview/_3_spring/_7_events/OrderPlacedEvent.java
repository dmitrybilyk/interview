package com.conduct.interview._3_spring._7_events;

import org.springframework.context.ApplicationEvent;

public class OrderPlacedEvent extends ApplicationEvent {

    private final String orderId;

    public OrderPlacedEvent(Object source, String orderId) {
        super(source);
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
