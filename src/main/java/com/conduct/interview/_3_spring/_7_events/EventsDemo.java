package com.conduct.interview._3_spring._7_events;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EventsDemo {

    public static void main(String[] args) {
        try (ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "com/conduct/interview/_3_spring/_7_events/events-context.xml")) {

            System.out.println("publishing OrderPlacedEvent...");
            context.publishEvent(new OrderPlacedEvent(context, "order-42"));
        }
    }
}
