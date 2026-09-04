package com.conduct.interview._1_bases.multithreading.types_of_thread_executions.sync_async;

import java.util.concurrent.CompletableFuture;

/**
 * TRUE ASYNC: no blocking get() - steps run automatically once the task completes,
 * so the caller keeps going immediately.
 */
public class AsyncWithCompletableFuture {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("[Main] Starting async pipeline...");

        CompletableFuture.supplyAsync(() -> {
                    sleep(2000); // simulate long work
                    return "Task Complete!";
                })
                .thenApply(result -> result + " (Processed)")
                .thenAccept(finalResult -> System.out.println("[Async Thread] Final result: " + finalResult))
                .exceptionally(ex -> {
                    System.out.println("Oops! Something went wrong: " + ex.getMessage());
                    return null;
                });

        System.out.println("[Main] I am NOT blocked. Doing other work...");

        Thread.sleep(3000); // just so the program doesn't exit before the async task finishes
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
