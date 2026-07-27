package com.conduct.interview._1_bases.multithreading.types_of_thread_executions.sync_async;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncWithFuture {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        long start = System.currentTimeMillis();
        System.out.println("a");
        System.out.println("b");
        Future<?> future = executorService.submit(() -> {
            try {
                sleeping1000();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
//        sleeping1000();
        Thread.sleep(1000);
        future.get();
        System.out.println("c");
        System.out.println(System.currentTimeMillis() - start);
    }

    static void sleeping1000() throws InterruptedException {
        System.out.println("In Sleep100 - " + Thread.currentThread().getName());
        Thread.sleep(1000);
    }
}
