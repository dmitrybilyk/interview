package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * submit() returns immediately with a Future - the task keeps running in the background.
 * future.get() blocks until the result is ready (or rethrows the task's exception).
 *
 * This demo shows true parallelism: 3 tasks * 2 seconds each = ~2 seconds total (not 6)
 * because they run in parallel on a 3-thread pool.
 */
public class FutureDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(3);

        Future<Integer> future1 = executor.submit(() -> {
            System.out.println("Task 1 started on " + Thread.currentThread().getName());
            Thread.sleep(2000);
            System.out.println("Task 1 finished");
            return 1;
        });

        Future<Integer> future2 = executor.submit(() -> {
            System.out.println("Task 2 started on " + Thread.currentThread().getName());
            Thread.sleep(2000);
            System.out.println("Task 2 finished");
            return 2;
        });

        Future<Integer> future3 = executor.submit(() -> {
            System.out.println("Task 3 started on " + Thread.currentThread().getName());
            Thread.sleep(2000);
            System.out.println("Task 3 finished");
            return 3;
        });

        System.out.println("Main thread doing other work...");
        Thread.sleep(1000);
        System.out.println("Main thread work done");

        Integer result1 = future1.get();
        Integer result2 = future2.get();
        Integer result3 = future3.get();

        System.out.println("Result 1: " + result1);
        System.out.println("Result 2: " + result2);
        System.out.println("Result 3: " + result3);

        long endTime = System.currentTimeMillis();
        System.out.println("Total time: " + (endTime - startTime) + " ms");

        executor.shutdown();
    }
}
