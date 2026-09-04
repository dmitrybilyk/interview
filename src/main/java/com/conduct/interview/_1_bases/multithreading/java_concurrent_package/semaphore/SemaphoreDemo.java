package com.conduct.interview._1_bases.multithreading.java_concurrent_package.semaphore;

import java.util.concurrent.Semaphore;

/**
 * Semaphore caps how many threads can use a resource at once - here, only 2 "spots" exist,
 * so with 5 workers, 3 of them have to wait for a spot to free up.
 */
public class SemaphoreDemo {

    public static void main(String[] args) {
        Semaphore parkingSpots = new Semaphore(2);

        for (int i = 1; i <= 5; i++) {
            int workerId = i;
            new Thread(() -> {
                try {
                    System.out.println("Worker " + workerId + " waiting for a spot...");
                    parkingSpots.acquire();
                    System.out.println("Worker " + workerId + " got a spot");

                    Thread.sleep(1000); // simulate using the resource

                    System.out.println("Worker " + workerId + " leaving");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    parkingSpots.release();
                }
            }).start();
        }
    }
}
