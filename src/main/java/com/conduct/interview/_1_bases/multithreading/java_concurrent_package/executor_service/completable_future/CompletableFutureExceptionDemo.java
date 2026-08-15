package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.completable_future;

import java.util.concurrent.CompletableFuture;

/**
 * exceptionally()/handle() let a chain recover from a failure and keep going -
 * unlike Future, where a failure just breaks your one call to get().
 */
public class CompletableFutureExceptionDemo {

    public static void main(String[] args) throws InterruptedException {

        // --- exceptionally(): only called on failure, supplies a fallback value ---
        CompletableFuture.<Integer>supplyAsync(() -> {
                    System.out.println("Task running...");
                    throw new RuntimeException("DB call failed");
                })
                .exceptionally(ex -> {
                    System.out.println("Recovered from: " + ex.getMessage());
                    return -1; // fallback value - the chain is NOT broken, it continues with this
                })
                .thenApply(result -> result * 2) // still runs, even though the first step failed
                .thenAccept(result -> System.out.println("Final result: " + result));

        Thread.sleep(500);
        System.out.println("---");

        // --- handle(): called on EITHER success or failure, in one place ---
        CompletableFuture.<Integer>supplyAsync(() -> {
                    System.out.println("Task 2 running...");
                    throw new RuntimeException("API call failed");
                })
                .handle((result, ex) -> {
                    if (ex != null) {
                        System.out.println("Handled failure: " + ex.getMessage());
                        return -1;
                    }
                    return result; // success path would come through here instead
                })
                .thenAccept(result -> System.out.println("Final result 2: " + result));

        Thread.sleep(500);
    }
}
