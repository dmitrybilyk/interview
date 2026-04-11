package com.conduct.interview._1_bases.multithreading.common_issues.livelock;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Livelock Demo
 * * We use a CyclicBarrier to force the threads to remain in lock-step.
 * This prevents the JVM/OS scheduler from accidentally "breaking"
 * the livelock by giving one thread more CPU time than the other.
 */
public class LivelockDemo {
    private final Lock lock1 = new ReentrantLock();
    private final Lock lock2 = new ReentrantLock();

    // The barrier ensures both threads start and end their attempts together
    private final CyclicBarrier barrier = new CyclicBarrier(2);

    public static void main(String[] args) {
        LivelockDemo demo = new LivelockDemo();

        System.out.println("Starting Livelock Demo... (Press Ctrl+C to stop)");

        new Thread(() -> demo.doWork(demo.lock1, demo.lock2), "Thread-1").start();
        new Thread(() -> demo.doWork(demo.lock2, demo.lock1), "Thread-2").start();
    }

    public void doWork(Lock firstLock, Lock secondLock) {
        while (true) {
            try {
                // 1. Sync Point: Both threads start the iteration at the same time
                barrier.await();

                if (firstLock.tryLock()) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " acquired " + getLockName(firstLock));

                        // 2. Give the other thread time to acquire its first lock
                        Thread.sleep(100);

                        if (secondLock.tryLock()) {
                            try {
                                System.out.println("--- SUCCESS! " + Thread.currentThread().getName() + " captured both! ---");
                                return;
                            } finally {
                                secondLock.unlock();
                            }
                        } else {
                            System.out.println(Thread.currentThread().getName() + " failed to get " + getLockName(secondLock)
                                    + ". It is held by the other thread.");
                        }
                    } finally {
                        // 3. Sync Point: Both threads wait until they both TRIED before releasing
                        // This prevents one thread from releasing and re-acquiring before the other reacts.
                        barrier.await();
                        firstLock.unlock();
                        System.out.println(Thread.currentThread().getName() + " released " + getLockName(firstLock) + " to be 'polite'.");
                    }
                }

                // Small gap between iterations
                Thread.sleep(10);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getLockName(Lock lock) {
        return (lock == lock1) ? "Lock-1" : "Lock-2";
    }
}