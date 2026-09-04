package com.conduct.interview._1_bases.multithreading.java_concurrent_package.thread_factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * ThreadFactory controls how a pool's threads get created: name, daemon flag, and what
 * happens if a task throws - not just naming.
 */
public class ThreadFactoryDemo {

    public static void main(String[] args) throws InterruptedException {
        ThreadFactory factory = runnable -> {
            Thread thread = new Thread(runnable, "worker-thread");
            thread.setDaemon(false);
            thread.setUncaughtExceptionHandler((t, e) ->
                    System.out.println("Caught from " + t.getName() + ": " + e));
            return thread;
        };

        ExecutorService executor = Executors.newFixedThreadPool(1, factory);

        executor.execute(() -> System.out.println("Running on " + Thread.currentThread().getName()));
        executor.execute(() -> {
            throw new RuntimeException("Task failed"); // normally just dumps to stderr and vanishes
        });

        executor.shutdown();
        executor.awaitTermination(1, java.util.concurrent.TimeUnit.SECONDS);
    }
}
