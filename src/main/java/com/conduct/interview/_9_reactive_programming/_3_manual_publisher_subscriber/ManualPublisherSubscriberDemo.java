package com.conduct.interview._9_reactive_programming._3_manual_publisher_subscriber;

import java.util.List;

public class ManualPublisherSubscriberDemo {

    public static void main(String[] args) {
        SimplePublisher<Integer> publisher =
                new SimplePublisher<>(List.of(1, 2, 3, 4, 5, 6, 7));

        publisher.subscribe(new LoggingSubscriber<>(3));
    }
}
