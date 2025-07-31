package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.future;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ForFuture {
    public static void main(String[] args) throws InterruptedException {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(() -> {
                Thread.sleep(2000);
                return "Result";
            }
        );

        for (int i = 0; i < 10; i++) {
            System.out.println("Doing some work");
        }

        Thread.sleep(3000);

        if (future.isDone()) {
            System.out.println("Done");
        } else {
            System.out.println("Not done");
        }
    }
}
