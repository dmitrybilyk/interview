package com.conduct.interview._1_bases.multithreading.common_issues.improper_thread_pool_usage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

public class BadThreadPoolDemo {

    public static void main(String[] args) {

        System.out.println("Starting BAD Thread Pool Demo...");
        System.out.println("This will create a thread pool that is WAY too big!");

        // BAD PRACTICE: Creating a huge fixed thread pool
        ExecutorService executor = Executors.newFixedThreadPool(5000);   // ← Too big!

        AtomicLong taskCount = new AtomicLong(0);
        AtomicLong completedCount = new AtomicLong(0);

        // Monitoring thread
        new Thread(() -> {
            while (true) {
                System.out.printf("[%s] Active Threads: %d | Tasks Submitted: %,d | Completed: %,d%n",
                        java.time.LocalTime.now(),
                        Thread.activeCount(),
                        taskCount.get(),
                        completedCount.get());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();

        // Simulate incoming requests (like a web server)
        for (int i = 0; i < 20_000; i++) {   // Submit 20,000 tasks
            final int taskId = i;
            executor.submit(() -> {
                taskCount.incrementAndGet();
                try {
                    // Simulate real work (DB call, external API, file read, etc.)
                    Thread.sleep(2000);        // Each task takes 2 seconds
                    System.out.println("Task " + taskId + " completed by " + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    completedCount.incrementAndGet();
                }
            });
        }

        System.out.println("All tasks submitted. Now waiting...");
    }
}