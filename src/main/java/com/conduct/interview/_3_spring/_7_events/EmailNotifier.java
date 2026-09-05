package com.conduct.interview._3_spring._7_events;

import org.springframework.context.event.EventListener;

/** Annotation style: needs EventListenerMethodProcessor registered (<context:annotation-config/>). */
public class EmailNotifier {

    @EventListener
    public void onOrderPlaced(OrderPlacedEvent event) {
        System.out.println("EmailNotifier (@EventListener style) -> emailing confirmation for order "
                + event.getOrderId());
    }
}
