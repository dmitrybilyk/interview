package com.conduct.interview._9_reactive_programming._3_manual_publisher_subscriber;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The engine of the pipeline. Emits items one at a time, but never more
 * than have been requested. Demand and emission are decoupled through a
 * "drain loop" so that request() can safely be called again from inside
 * onNext() without recursing - see manual_publisher_subscriber.md.
 */
public class SimpleSubscription<T> implements Subscription {

    private final List<T> items;
    private final Subscriber<? super T> subscriber;

    private final AtomicLong requested = new AtomicLong();
    private final AtomicInteger wip = new AtomicInteger(); // 0 = idle, >0 = someone is draining
    private int index = 0;
    private volatile boolean cancelled;

    SimpleSubscription(List<T> items, Subscriber<? super T> subscriber) {
        this.items = items;
        this.subscriber = subscriber;
    }

    @Override
    public void request(long n) {
        if (n <= 0) {
            subscriber.onError(new IllegalArgumentException(
                    "Reactive Streams rule 3.9: request(n) must be positive, got " + n));
            return;
        }
        addCapped(requested, n);
        drain();
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    /**
     * Only one invocation of this loop is ever actively emitting at a time.
     * A request() that arrives while a drain is already running just bumps
     * `requested` and returns - the running loop will notice and keep going.
     */
    private void drain() {
        if (wip.getAndIncrement() != 0) {
            return;
        }
        int missed = 1;
        for (;;) {
            long demand = requested.get();
            long emitted = 0;

            while (emitted < demand) {
                if (cancelled) {
                    return;
                }
                if (index >= items.size()) {
                    subscriber.onComplete();
                    return;
                }
                subscriber.onNext(items.get(index++));
                emitted++;
            }

            if (emitted > 0) {
                requested.addAndGet(-emitted);
            }

            missed = wip.addAndGet(-missed);
            if (missed == 0) {
                return;
            }
        }
    }

    private static void addCapped(AtomicLong requested, long n) {
        long current;
        do {
            current = requested.get();
            if (current == Long.MAX_VALUE) {
                return;
            }
        } while (!requested.compareAndSet(current, addNoOverflow(current, n)));
    }

    private static long addNoOverflow(long a, long b) {
        long sum = a + b;
        return sum >= 0 ? sum : Long.MAX_VALUE;
    }
}
