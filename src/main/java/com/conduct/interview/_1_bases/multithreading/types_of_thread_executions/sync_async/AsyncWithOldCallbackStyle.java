package com.conduct.interview._1_bases.multithreading.types_of_thread_executions.sync_async;

import java.util.function.Consumer;

/**
 * Pre-Future way of doing async: pass a callback instead of returning a value.
 * Works, but chaining several of these gets messy fast ("callback hell") - that's
 * what Future/CompletableFuture were built to replace.
 */
public class AsyncWithOldCallbackStyle {

    public static void main(String[] args) throws InterruptedException {
        asyncOperation(result -> System.out.println("Callback result received: " + result));

        System.out.println("Main thread continues...");
        Thread.sleep(2000); // just so the program doesn't exit before the callback fires
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