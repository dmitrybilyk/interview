package com.conduct.interview._9_reactive_programming._3_manual_publisher_subscriber;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.List;

/**
 * The simplest possible Publisher: a fixed, already-known list of items.
 * A Publisher never pushes anything by itself - subscribe() only performs
 * the handshake (onSubscribe). All emission happens later, driven by the
 * Subscription's request(n) calls.
 */
public class SimplePublisher<T> implements Publisher<T> {

    private final List<T> items;

    public SimplePublisher(List<T> items) {
        this.items = items;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        SimpleSubscription<T> subscription = new SimpleSubscription<>(items, subscriber);
        subscriber.onSubscribe(subscription);
    }
}
