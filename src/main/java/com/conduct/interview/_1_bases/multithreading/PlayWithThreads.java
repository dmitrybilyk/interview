package com.conduct.interview._1_bases.multithreading;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayWithThreads {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        ExecutorService executorService2 = Executors.newFixedThreadPool(3);


        System.out.println("Main started - " + Thread.currentThread().getName());
        CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Task 1 started - " + Thread.currentThread().getName());
                Thread.sleep(1000);
                System.out.println("Task 1 completed - " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "Result";
        }).thenApplyAsync(result -> {
            System.out.println("Task 2 started - " + Thread.currentThread().getName());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Task 2 completed - " + Thread.currentThread().getName());
            return result + "Result2";
        }, executorService).thenAccept(result -> {
            System.out.println("Tasks are done, result - " + result + " - " + Thread.currentThread().getName());
        });

        Thread.sleep(4000);

    }
}
