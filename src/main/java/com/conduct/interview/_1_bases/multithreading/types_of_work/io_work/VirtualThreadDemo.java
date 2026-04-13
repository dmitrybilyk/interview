package com.conduct.interview._1_bases.multithreading.types_of_work.io_work;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VirtualThreadDemo {
    public static void main(String[] args) throws InterruptedException {
        int totalTasks = 50;

        // Uses Virtual Threads! No fixed limit on "waiters"
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            
            long start = System.currentTimeMillis();
            System.out.println("Submitting " + totalTasks + " tasks using Virtual Threads...");

            for (int i = 0; i < totalTasks; i++) {
                executor.submit(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
            
            long end = System.currentTimeMillis();
            System.out.println("Total time with Virtual Threads: " + (end - start) + " ms");
        }
    }
}