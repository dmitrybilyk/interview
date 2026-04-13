package com.conduct.interview._1_bases.multithreading.common_issues.improper_thread_pool_usage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPools {

    public static void main(String[] args) {

        Runnable runnable = () -> {
            System.out.println("Task executed by: " + Thread.currentThread().getName());
            try {
                Thread.sleep(100);   // simulate some work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        // 1. Create Thread Pool
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try {
            System.out.println("Submitting 100 tasks...");

            // Submit tasks
            for (int i = 1; i <= 100; i++) {
                executorService.submit(runnable);
            }

            System.out.println("All tasks submitted.");

        } finally {
            // === VERY IMPORTANT: Proper Shutdown Sequence ===
            shutdownAndAwaitTermination(executorService);
        }
    }

    /**
     * Best practice: Utility method for safe thread pool shutdown
     */
    private static void shutdownAndAwaitTermination(ExecutorService executor) {
        if (executor == null) return;

        System.out.println("Shutting down thread pool...");

        try {
            // Step 1: Stop accepting new tasks
            executor.shutdown();

            // Step 2: Wait for existing tasks to complete (graceful shutdown)
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                System.out.println("Tasks did not finish in time. Forcing shutdown...");

                // Step 3: Force shutdown - interrupt all running threads
                executor.shutdownNow();

                // Step 4: Wait a little more for threads to respond to interrupt
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("WARNING: Some tasks did not terminate even after shutdownNow()");
                }
            } else {
                System.out.println("Thread pool shut down gracefully.");
            }

        } catch (InterruptedException e) {
            System.out.println("Shutdown was interrupted. Forcing shutdown now.");
            executor.shutdownNow();

            // Restore interrupt status - very important!
            Thread.currentThread().interrupt();
        }
    }
}