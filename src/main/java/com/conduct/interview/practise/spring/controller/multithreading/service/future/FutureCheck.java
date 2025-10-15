package com.conduct.interview.practise.spring.controller.multithreading.service.future;

import java.util.List;
import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class FutureCheck {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

//        ExecutorService executorService = Executors.newFixedThreadPool(5);
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        Runnable runnable = () -> {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        try {
            // Submit tasks and collect futures
            List<Future<?>> futures = new java.util.ArrayList<>();
            for (int i = 0; i < 50; i++) {
                futures.add(executorService.submit(runnable));
            }

            // Wait for all futures to complete
            for (Future<?> future : futures) {
                try {
                    future.get(); // blocks until task completes
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        } finally {
            // Gracefully shut down
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        long end = System.currentTimeMillis();
        System.out.println("Total execution time: " + (end - start) + " ms");
    }
}
