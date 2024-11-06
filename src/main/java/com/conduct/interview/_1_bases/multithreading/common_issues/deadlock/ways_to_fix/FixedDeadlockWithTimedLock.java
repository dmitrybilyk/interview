package com.conduct.interview._1_bases.multithreading.common_issues.deadlock.ways_to_fix;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

public class FixedDeadlockWithTimedLock {
    static class A {}
    static class B {}

    static A objA = new A();
    static B objB = new B();
    static ReentrantLock lockA = new ReentrantLock();
    static ReentrantLock lockB = new ReentrantLock();

    public static void main(String[] args) {
        // Thread 1: Tries to lock objA then objB with a timeout
        Runnable task1 = () -> {
            try {
                // Try to lock objA with a timeout
                if (lockA.tryLock(100, TimeUnit.MILLISECONDS)) {
                    System.out.println("Thread 1: Acquired lock on objA...");
                    try { Thread.sleep(100); } catch (InterruptedException e) {}

                    // Try to lock objB with a timeout
                    if (lockB.tryLock(100, TimeUnit.MILLISECONDS)) {
                        System.out.println("Thread 1: Acquired lock on objB.");
                    } else {
                        System.out.println("Thread 1: Could not acquire lock on objB, retrying...");
                        // Back off if it couldn't acquire objB
                    }
                } else {
                    System.out.println("Thread 1: Could not acquire lock on objA, retrying...");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // Ensure locks are released safely
                if (lockA.isHeldByCurrentThread()) lockA.unlock();
                if (lockB.isHeldByCurrentThread()) lockB.unlock();
            }
        };

        // Thread 2: Tries to lock objB then objA with a timeout
        Runnable task2 = () -> {
            try {
                // Try to lock objB with a timeout
                if (lockB.tryLock(100, TimeUnit.MILLISECONDS)) {
                    System.out.println("Thread 2: Acquired lock on objB...");
                    try { Thread.sleep(100); } catch (InterruptedException e) {}

                    // Try to lock objA with a timeout
                    if (lockA.tryLock(100, TimeUnit.MILLISECONDS)) {
                        System.out.println("Thread 2: Acquired lock on objA.");
                    } else {
                        System.out.println("Thread 2: Could not acquire lock on objA, retrying...");
                        // Back off if it couldn't acquire objA
                    }
                } else {
                    System.out.println("Thread 2: Could not acquire lock on objB, retrying...");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                // Ensure locks are released safely
                if (lockA.isHeldByCurrentThread()) lockA.unlock();
                if (lockB.isHeldByCurrentThread()) lockB.unlock();
            }
        };

        // Start both threads
        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);

        thread1.start();
        thread2.start();
    }
}
