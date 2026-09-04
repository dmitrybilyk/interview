package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.locks.stamped_locks.stamped_with_optimistic_lock;

import java.util.concurrent.locks.StampedLock;

/**
 * Optimistic read: don't lock at all, just read, then check if a write happened meanwhile
 * (validate()). If nothing changed, the value was consistent - no reader ever blocked a writer.
 * If something did change, fall back to a real read lock and read again.
 */
public class StampedWithOptimisticLockCounter {

    private int counter;
    private final StampedLock lock = new StampedLock();

    public void increment() {
        long stamp = lock.writeLock();
        try {
            counter++;
        } finally {
            lock.unlock(stamp);
        }
    }

    public int getCounter() {
        long stamp = lock.tryOptimisticRead(); // no lock taken
        int value = counter;

        if (!lock.validate(stamp)) {
            // a write happened between the read and the validate check - read safely instead
            stamp = lock.readLock();
            try {
                value = counter;
            } finally {
                lock.unlock(stamp);
            }
        }
        return value;
    }

    public static void main(String[] args) throws InterruptedException {
        StampedWithOptimisticLockCounter counter = new StampedWithOptimisticLockCounter();

        Thread writer = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        });

        Thread reader = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Optimistic read: " + counter.getCounter());
            }
        });

        writer.start();
        reader.start();
        writer.join();
        reader.join();

        System.out.println("Final (expected 1000): " + counter.getCounter());
    }
}
