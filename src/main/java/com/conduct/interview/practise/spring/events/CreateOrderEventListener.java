package com.conduct.interview.practise.spring.events;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class CreateOrderEventListener {
    @EventListener
    public void orderCreated(CreateOrderEvent event) {
        System.out.println(event.getOrderIdentifier());
    }
}
