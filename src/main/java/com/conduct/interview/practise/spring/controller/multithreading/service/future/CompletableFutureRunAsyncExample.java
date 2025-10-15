package com.conduct.interview.practise.spring.controller.multithreading.service.future;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureRunAsyncExample {
    public static void main(String[] args) {
        System.out.println("Main thread: " + Thread.currentThread().getName());

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("Async task running on: " + Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Async task finished");
        });

        System.out.println("Main continues doing other work...");

        // Wait for async task to complete
        future.join();

        System.out.println("Main done!");
    }
}
