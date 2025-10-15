package com.conduct.interview.practise.spring.controller.multithreading.service.future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static java.lang.Thread.sleep;

public class CompletableFutureSupplyAsyncExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("Main thread: " + Thread.currentThread().getName());

        CompletableFuture<Integer> integerCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 100;
        });
        System.out.println(integerCompletableFuture.get());

        // Run async computation
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("Async task running on: " + Thread.currentThread().getName());
            try {
                sleep(1000); // simulate slow work
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "Result from async task";
        });

        System.out.println("Main continues doing other work...");

        // Block and get the result
        String result = future.join();

        System.out.println("Got result: " + result);
        System.out.println("Main ends!");
    }
}
