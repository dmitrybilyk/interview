package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * submit() returns immediately with a Future - the task keeps running in the background.
 * future.get() blocks until the result is ready (or rethrows the task's exception).
 */
public class FutureDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Future<Integer> future = executor.submit(() -> {
            Thread.sleep(1000);
            return 42;
        });

        System.out.println("isDone right after submit: " + future.isDone()); // false, task still running

        Integer result = future.get(); // blocks here until the task finishes
        System.out.println("Result: " + result);

        executor.shutdown();
    }
}
