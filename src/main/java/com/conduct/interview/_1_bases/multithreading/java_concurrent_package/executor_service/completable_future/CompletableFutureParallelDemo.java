package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.completable_future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 3 tasks (2s each) run in parallel via CompletableFuture while the main thread keeps doing
 * its own work too - total time should be ~2s (the slowest task), not 2s*3 + 1s sequentially.
 *
 * Uses an explicit executor instead of the default common pool: guarantees real parallelism
 * (common pool's default size can be as low as 1 on a constrained machine) and its threads
 * aren't daemon threads, so there's no risk of work being silently dropped (see completable_future.md).
 */
public class CompletableFutureParallelDemo {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        try (ExecutorService executor = Executors.newFixedThreadPool(3)) {
            CompletableFuture<Integer> task1 = CompletableFuture.supplyAsync(() -> work("Task 1"), executor);
            CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> work("Task 2"), executor);
            CompletableFuture<Integer> task3 = CompletableFuture.supplyAsync(() -> work("Task 3"), executor);

            System.out.println("Main thread doing other work...");
            sleep(1000);
            System.out.println("Main thread work done");

            // allOf() completes once all 3 are done; join() here blocks until that happens
            CompletableFuture.allOf(task1, task2, task3).join();

            // each individual join() returns immediately now - the result is already there
            int total = task1.join() + task2.join() + task3.join();
            System.out.println("Sum of results: " + total);
        }

        System.out.println("Total time: " + (System.currentTimeMillis() - start) + " ms");
    }

    private static Integer work(String name) {
        System.out.println(name + " started on " + Thread.currentThread().getName());
        sleep(2000);
        System.out.println(name + " finished");
        return 1;
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
