package com.conduct.interview._1_bases.multithreading.common_issues.deadlock.ways_to_fix;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FixedTryLockDeadlockWithTryLock {
    static class A {}
    static class B {}

    static A objA = new A();
    static B objB = new B();
    static ReentrantLock lockA = new ReentrantLock();
    static ReentrantLock lockB = new ReentrantLock();

    public static void main(String[] args) {
        // Thread 1: Tries to lock objA then objB
        Runnable task1 = () -> {
            try {
                if (lockA.tryLock() && lockB.tryLock()) {
                    System.out.println("Thread 1: Acquired lock on objA and objB.");
                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                } else {
                    System.out.println("Thread 1: Could not acquire both locks, retrying...");
                }
            } finally {
                if (lockA.isHeldByCurrentThread()) lockA.unlock();
                if (lockB.isHeldByCurrentThread()) lockB.unlock();
            }
        };

        // Thread 2: Tries to lock objA then objB
        Runnable task2 = () -> {
            try {
                if (lockA.tryLock() && lockB.tryLock()) {
                    System.out.println("Thread 2: Acquired lock on objA and objB.");
                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                } else {
                    System.out.println("Thread 2: Could not acquire both locks, retrying...");
                }
            } finally {
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
