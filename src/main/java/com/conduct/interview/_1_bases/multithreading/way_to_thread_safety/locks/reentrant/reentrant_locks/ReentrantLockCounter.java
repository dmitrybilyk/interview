package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.locks.reentrant.reentrant_locks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Explicit Lock instead of synchronized - same mutual-exclusion effect, but lock()/unlock()
 * are separate calls, so the critical section can be more precise (and must always unlock in
 * a finally block, or a thrown exception leaves the lock held forever).
 */
public class ReentrantLockCounter {

    private int counter;
    private final ReentrantLock lock = new ReentrantLock();

    public void increment() {
        lock.lock();
        try {
            counter++;
        } finally {
            lock.unlock();
        }
    }

    public int getCounter() {
        return counter;
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        ReentrantLockCounter counter = new ReentrantLockCounter();

        for (int i = 0; i < 1000; i++) {
            executor.submit(counter::increment);
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);

        System.out.println("Expected: 1000");
        System.out.println("Actual:   " + counter.getCounter());
    }
}
