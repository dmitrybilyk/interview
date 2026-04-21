package com.conduct.interview._1_bases.multithreading.types_of_thread_executions.sync_async;

import java.util.function.Consumer;

public class AsyncWithOldCallbackStyle {

    public static void main(String[] args) throws InterruptedException {
        asyncOperation(result -> {
            System.out.println("Callback received: " + result);
        });

        System.out.println("Main thread continues...");
        Thread.sleep(2000); // щоб побачити результат
    }

    static void asyncOperation(Consumer<String> callback) {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            callback.accept("Hello from async task");
        }).start();
    }
}