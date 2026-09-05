package com.conduct.interview._9_reactive_programming._3_manual_publisher_subscriber;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * Deliberately asks for a small batch at a time instead of
 * Long.MAX_VALUE, so the request/onNext ping-pong from
 * manual_publisher_subscriber.md is visible in the console output.
 */
public class LoggingSubscriber<T> implements Subscriber<T> {

    private final int batchSize;
    private Subscription subscription;
    private int receivedInBatch;

    public LoggingSubscriber(int batchSize) {
        this.batchSize = batchSize;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        System.out.println("onSubscribe -> request(" + batchSize + ")");
        subscription.request(batchSize);
    }

    @Override
    public void onNext(T item) {
        System.out.println("onNext(" + item + ")");
        receivedInBatch++;
        if (receivedInBatch == batchSize) {
            receivedInBatch = 0;
            System.out.println("  batch done -> request(" + batchSize + ")");
            subscription.request(batchSize); // re-entrant call, handled by the drain loop
        }
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("onError: " + t);
    }

    @Override
    public void onComplete() {
        System.out.println("onComplete");
    }
}
