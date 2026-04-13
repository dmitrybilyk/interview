package com.conduct.interview._1_bases.multithreading.types_of_work.io_work;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BlockingWaitDemo {
    public static void main(String[] args) throws InterruptedException {
        int totalTasks = 50;
        // Only 10 threads available!
        try (ExecutorService executor = Executors.newFixedThreadPool(10)) {
            
            long start = System.currentTimeMillis();
            System.out.println("Submitting " + totalTasks + " tasks (each takes 1s blocking)...");

            for (int i = 0; i < totalTasks; i++) {
                executor.submit(() -> {
                    try {
                        // Simulate blocking I/O (like an HTTP call)
                        Thread.sleep(1000); 
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
            
            long end = System.currentTimeMillis();
            System.out.println("Total time with 10 platform threads: " + (end - start) + " ms");
        }
    }
}