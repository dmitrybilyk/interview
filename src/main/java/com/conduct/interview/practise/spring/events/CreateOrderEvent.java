package com.conduct.interview.practise.spring.events;

public class CreateOrderEvent {
    private String orderIdentifier;
    public CreateOrderEvent(String orderIdentifier) {
        this.orderIdentifier = orderIdentifier;
    }

    public String getOrderIdentifier() {
        return orderIdentifier;
    }
}
