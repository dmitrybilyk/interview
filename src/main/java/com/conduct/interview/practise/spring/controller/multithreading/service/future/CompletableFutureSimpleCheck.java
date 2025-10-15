package com.conduct.interview.practise.spring.controller.multithreading.service.future;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureSimpleCheck {
    public static void main(String[] args) {
        System.out.println("Main starts...");

        // Run async task (non-blocking)
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1000); // simulate slow work
                System.out.println("Async task completed!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Wait for completion
        future.join();

        System.out.println("Main ends!");
    }
}
