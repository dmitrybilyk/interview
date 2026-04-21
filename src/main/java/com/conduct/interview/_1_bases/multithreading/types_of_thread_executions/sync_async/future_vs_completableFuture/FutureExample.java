package com.conduct.interview._1_bases.multithreading.types_of_thread_executions.sync_async.future_vs_completableFuture;

import java.util.concurrent.*;

public class FutureExample {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        System.out.println("[Main] Submitting task...");
        
        Future<String> future = executor.submit(() -> {
            Thread.sleep(2000); // Simulate long work
            return "Task Complete!";
        });

        // ❌ BLOCKING: The main thread stops here and waits.
        // It cannot do anything else until the 2 seconds are up.
        System.out.println("[Main] Waiting for result (I am blocked now)...");
        String result = future.get(); 

        System.out.println("[Main] Got result: " + result);
        executor.shutdown();
    }
}