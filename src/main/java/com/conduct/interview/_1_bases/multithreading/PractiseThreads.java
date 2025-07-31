package com.conduct.interview._1_bases.multithreading;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class PractiseThreads {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        CommonResource commonResource = new CommonResource();
        Thread thread = new Thread(new MyRunnable(commonResource));
        Thread thread2 = new Thread(new MyRunnable(commonResource));
        thread.start();
        thread2.start();
        thread.join();
        thread2.join();
        System.out.println("main thread started");
        System.out.println(commonResource.getCount());

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try {
            Future<?> submit = executorService.submit(new MyRunnable(commonResource));
            submit.get();
        } finally {
            executorService.shutdown();
        }
        System.out.println(commonResource.getCount());

//        CompletableFuture<Void> completableFuture = CompletableFuture
//                .runAsync(new MyRunnable(commonResource))
//                .thenAccept(_ -> System.out.println(commonResource.getCount()));
//        completableFuture.get();

        CompletableFuture.supplyAsync(() -> "Hello")
                .thenApply(s -> s + " world")
                .thenAccept(System.out::println);
    }
}

@AllArgsConstructor
class MyRunnable implements Runnable {
    private CommonResource commonResource;
    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            commonResource.increment();
        }
    }
}

@Data
class CommonResource {
    private int count;
    private ReentrantLock lock = new ReentrantLock();
    public void increment() {
        try {
            lock.lock();
            count += 1;
        } finally {
            lock.unlock();
        }

    }
}
