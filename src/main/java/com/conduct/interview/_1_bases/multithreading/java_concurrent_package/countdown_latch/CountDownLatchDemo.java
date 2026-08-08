package com.conduct.interview._1_bases.multithreading.java_concurrent_package.countdown_latch;

import java.util.concurrent.CountDownLatch;

/**
 * Lets one thread wait until N other things finish - each worker counts down once,
 * await() unblocks only when the count reaches zero.
 */
public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {
        int workerCount = 3;
        CountDownLatch latch = new CountDownLatch(workerCount);

        for (int i = 1; i <= workerCount; i++) {
            int workerId = i;
            new Thread(() -> {
                System.out.println("Worker " + workerId + " starting...");
                try {
                    Thread.sleep(workerId * 300L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Worker " + workerId + " done");
                latch.countDown();
            }).start();
        }

        System.out.println("Main: waiting for all workers...");
        latch.await(); // blocks until count reaches 0
        System.out.println("Main: all workers finished, continuing");
    }
}
