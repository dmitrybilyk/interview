package com.conduct.interview._1_bases.multithreading.java_concurrent_package.cyclic_barrier;

import java.util.concurrent.CyclicBarrier;

/**
 * Makes a fixed number of threads wait for each other at a common point before any of them
 * continue - unlike CountDownLatch, the barrier resets automatically for reuse.
 */
public class CyclicBarrierDemo {

    public static void main(String[] args) {
        int parties = 3;
        CyclicBarrier barrier = new CyclicBarrier(parties,
                () -> System.out.println("All workers reached the barrier, proceeding together"));

        for (int i = 1; i <= parties; i++) {
            int workerId = i;
            new Thread(() -> {
                try {
                    System.out.println("Worker " + workerId + " doing phase 1...");
                    Thread.sleep(workerId * 200L);
                    System.out.println("Worker " + workerId + " waiting at barrier");
                    barrier.await(); // blocks until all 3 workers call await()

                    System.out.println("Worker " + workerId + " doing phase 2...");
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}
