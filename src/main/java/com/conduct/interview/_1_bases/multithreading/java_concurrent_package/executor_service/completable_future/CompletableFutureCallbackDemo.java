package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.completable_future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Same 3 parallel tasks as CompletableFutureParallelDemo, but NO join()/get() anywhere -
 * the "what to do with the result" is handed to thenAccept() as a callback instead.
 * Watch the T+ms timestamps: main's own lines finish almost instantly, the callback
 * fires ~2 seconds later, on a pool thread, after main has already moved on.
 */
public class CompletableFutureCallbackDemo {

    private static final long START = System.currentTimeMillis();

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        CompletableFuture<Integer> task1 = CompletableFuture.supplyAsync(() -> work("Task 1"), executor);
        CompletableFuture<Integer> task2 = CompletableFuture.supplyAsync(() -> work("Task 2"), executor);
        CompletableFuture<Integer> task3 = CompletableFuture.supplyAsync(() -> work("Task 3"), executor);

        // Not blocking here: this just registers "run this LATER, whenever all 3 are done".
        // main() does not stop and wait for it - it moves to the next line immediately.
        CompletableFuture.allOf(task1, task2, task3)
                .thenApply(v -> task1.join() + task2.join() + task3.join()) // join() here is instant, allOf already guaranteed completion
                .thenAccept(total -> log("CALLBACK fired, sum = " + total));

        log("Main: fired off the async chain, NOT waiting - moving on immediately");
        log("Main: doing its own unrelated work now...");

        // Only reason for this sleep: keep the JVM alive long enough to see the callback
        // print (see completable_future.md's daemon-thread note) - it's not "waiting for the result",
        // main has no idea if/when the callback runs.
        Thread.sleep(3000);
        log("Main: done sleeping, exiting");
        executor.shutdown();
    }

    private static Integer work(String name) {
        log(name + " started on " + Thread.currentThread().getName());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        log(name + " finished");
        return 1;
    }

    private static void log(String message) {
        System.out.println("T+" + (System.currentTimeMillis() - START) + "ms  [" + Thread.currentThread().getName() + "]  " + message);
    }
}
