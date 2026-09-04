package com.conduct.interview._1_bases.multithreading.types_of_thread_executions.sync_async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * SYNC-ish: the task itself runs on another thread, but future.get() blocks the
 * caller until it's done - so from the caller's point of view, it still waits.
 */
public class AsyncWithFuture {

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        System.out.println("[Main] Submitting task...");

        Future<String> future = executor.submit(() -> {
            Thread.sleep(2000); // simulate long work
            return "Task Complete!";
        });

        System.out.println("[Main] Waiting for result (blocked now)...");
        String result = future.get(); // blocks here

        System.out.println("[Main] Got result: " + result);
        executor.shutdown();
    }
}
