package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.locks.reentrant.reentrant_read_write_locks;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Many readers can hold the read lock at the same time (reads don't conflict with each other),
 * but a writer needs exclusive access - no readers or other writers allowed while writing.
 */
public class ReentrantReadWriteLockCounter {

    private int counter;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void increment() {
        lock.writeLock().lock();
        try {
            counter++;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getCounter() {
        lock.readLock().lock();
        try {
            return counter;
        } finally {
            lock.readLock().unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        ReentrantReadWriteLockCounter counter = new ReentrantReadWriteLockCounter();

        for (int i = 0; i < 1000; i++) {
            executor.submit(counter::increment);
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);

        System.out.println("Expected: 1000");
        System.out.println("Actual:   " + counter.getCounter());
    }
}
