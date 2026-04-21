package com.conduct.interview._1_bases.multithreading.types_of_thread_executions.sync_async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AsyncWithCompletableFuture {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();
        CompletableFuture<Void> completableFuture =
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
        
        Thread.sleep(1000);
        completableFuture.get();

        System.out.println(System.currentTimeMillis() - start);
    }
}
