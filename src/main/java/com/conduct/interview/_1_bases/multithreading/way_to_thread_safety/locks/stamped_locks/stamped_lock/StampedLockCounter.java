package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.locks.stamped_locks.stamped_lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.StampedLock;

/**
 * StampedLock is faster than ReentrantLock but NOT reentrant (locking twice from the same
 * thread deadlocks it), and unlock needs the exact stamp returned by lock() - not an object,
 * just a long, so nothing stops you from using the wrong one if you're not careful.
 */
public class StampedLockCounter {

    private int counter;
    private final StampedLock lock = new StampedLock();

    public void increment() {
        long stamp = lock.writeLock(); // must be writeLock() - readLock() would let other
        try {                          // readers/writers race the increment
            counter++;
        } finally {
            lock.unlock(stamp);
        }
    }

    public int getCounter() {
        long stamp = lock.readLock();
        try {
            return counter;
        } finally {
            lock.unlock(stamp);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        StampedLockCounter counter = new StampedLockCounter();

        for (int i = 0; i < 1000; i++) {
            executor.submit(counter::increment);
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);

        System.out.println("Expected: 1000");
        System.out.println("Actual:   " + counter.getCounter());
    }
}
