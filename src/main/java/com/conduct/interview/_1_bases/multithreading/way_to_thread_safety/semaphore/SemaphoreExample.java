package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Semaphore caps concurrent access to a resource - only 3 permits, so with 10 tasks,
 * 7 of them always have to wait for a permit to free up.
 */
public class SemaphoreExample {

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 1; i <= 10; i++) {
            int taskId = i;
            executor.execute(() -> {
                try {
                    System.out.println("Task " + taskId + " waiting for a permit...");
                    semaphore.acquire();
                    try {
                        System.out.println(">>> Task " + taskId + " got a permit, working");
                        Thread.sleep(2000); // simulate work, e.g. a heavy HTTP call
                    } finally {
                        System.out.println("<<< Task " + taskId + " done, releasing permit");
                        semaphore.release(); // always release, even on failure
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
    }
}
