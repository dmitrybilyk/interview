package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.completable_future;

import java.util.concurrent.CompletableFuture;

/**
 * Unlike a plain Future, steps are chained without ever calling a blocking get() -
 * each step runs automatically once the previous one completes.
 */
public class CompletableFutureDemo {

    public static void main(String[] args) throws InterruptedException {
        CompletableFuture.supplyAsync(() -> 40)
                .thenApply(result -> result + 2)
                .thenAccept(result -> System.out.println("Result: " + result));

        System.out.println("Main thread keeps going while the above runs in the background");

        Thread.sleep(500); // just so the demo doesn't exit before the async chain prints
    }
}
