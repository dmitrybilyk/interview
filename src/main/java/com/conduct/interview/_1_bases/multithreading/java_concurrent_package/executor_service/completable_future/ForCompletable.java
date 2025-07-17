package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.completable_future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ForCompletable {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 40;
        }).thenApply(result -> {
            System.out.println("Adding something");
            return result + 10;
        }).thenAccept(integer -> {
            System.out.println("Final result is " + integer);
        });

//        System.out.println(completableFuture.get());
        System.out.println("going further");

        Thread.sleep(3000);

    }
}
