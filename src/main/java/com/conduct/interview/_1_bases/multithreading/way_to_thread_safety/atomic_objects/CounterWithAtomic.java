package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.atomic_objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * FIX: AtomicInteger.incrementAndGet() is a single atomic operation, so nothing gets lost -
 * same test as the unsafe Counter, always gives the correct result.
 */
public class CounterWithAtomic {

    private final AtomicInteger counter = new AtomicInteger();

    public void increment() {
        counter.incrementAndGet();
    }

    public int getCounter() {
        return counter.get();
    }

    public static void main(String[] args) throws InterruptedException {
        CounterWithAtomic counter = new CounterWithAtomic();
        Runnable task = () -> {
            for (int i = 0; i < 5000; i++) {
                counter.increment();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Expected: 10000");
        System.out.println("Actual:   " + counter.getCounter());
    }
}
