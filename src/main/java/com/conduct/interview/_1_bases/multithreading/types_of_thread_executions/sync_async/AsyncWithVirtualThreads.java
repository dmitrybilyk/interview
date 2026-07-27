package com.conduct.interview._1_bases.multithreading.types_of_thread_executions.sync_async;

public class AsyncWithVirtualThreads {

    public static void main(String[] args) throws InterruptedException {
        long start = System.currentTimeMillis();

        Thread.startVirtualThread(() -> {
            try {
                Thread.sleep(1000);
                System.out.println("Virtual thread done: " + Thread.currentThread());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("Main thread continues...");
        Thread.sleep(1000);
        System.out.println(System.currentTimeMillis() - start);
    }
}