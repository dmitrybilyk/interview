package com.conduct.interview.practise.spring.controller.multithreading.service.future.check;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class CompletableFutureCheck {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CompletableFuture<Void> completableFuture =
                CompletableFuture.supplyAsync(() -> "result", executorService)
//                .thenRunAsync(() ->
//                        System.out.println("next step in thread - " +
//                                Thread.currentThread().getName()), executorService);
        .thenApply(s -> s + s)
                .thenAccept(result -> System.out.println(result + result));
//        System.out.println(completableFuture.get());
//        completableFuture.join();
        executorService.shutdownNow();
//        sleep(1100);
        System.out.println("in main end");
    }
}
