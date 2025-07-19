package com.conduct.interview.practise.spring.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class OrderCreateEventPublisher {
    private ApplicationEventPublisher eventPublisher;

    public OrderCreateEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publish(CreateOrderEvent event) {
        eventPublisher.publishEvent(event);
    }
}
