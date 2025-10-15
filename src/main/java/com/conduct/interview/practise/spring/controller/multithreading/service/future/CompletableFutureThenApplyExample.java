package com.conduct.interview.practise.spring.controller.multithreading.service.future;

import java.util.concurrent.CompletableFuture;

public class CompletableFutureThenApplyExample {
    public static void main(String[] args) {
        CompletableFuture<String> future = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println("Fetching data on: " + Thread.currentThread().getName());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return "java";
                })
                .thenApply(str -> {
                    System.out.println("Transforming on: " + Thread.currentThread().getName());
                    return str.toUpperCase();
                })
                .thenApply(str -> "Result: " + str);

        System.out.println("Main continues doing other work...");
        String result = future.join();
        System.out.println(result);
    }
}
