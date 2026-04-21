package com.conduct.interview._1_bases.multithreading.types_of_thread_executions.sync_async.future_vs_completableFuture;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureExample {
    public static void main(String[] args) {
        System.out.println("[Main] Starting async pipeline...");

        // ✅ NON-BLOCKING: We define the "pipeline" and move on.
        CompletableFuture.supplyAsync(() -> {
            try { Thread.sleep(2000); } catch (InterruptedException e) {}
            return "Task Complete!";
        })
        .thenApply(result -> result + " (Processed)") // Transform result
        .thenAccept(finalResult -> {
            System.out.println("[Async Thread] Final result: " + finalResult);
        })
        .exceptionally(ex -> {
            System.out.println("Oops! Something went wrong: " + ex.getMessage());
            return null;
        });

        // The main thread keeps running!
        System.out.println("[Main] I am NOT blocked. Doing other work...");
        
        // Just to prevent the program from closing before the async task finishes
        try { Thread.sleep(3000); } catch (InterruptedException e) {}
    }
}