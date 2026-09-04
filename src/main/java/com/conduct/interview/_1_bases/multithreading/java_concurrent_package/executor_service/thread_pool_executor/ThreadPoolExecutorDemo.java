package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.thread_pool_executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ThreadPoolExecutor is what Executors.newFixedThreadPool()/etc. actually create underneath -
 * this configures it by hand to show the 4 knobs that control its behavior.
 */
public class ThreadPoolExecutorDemo {

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,                                          // core pool size - always kept alive
                4,                                          // max pool size - hard ceiling
                5, TimeUnit.SECONDS,                        // idle timeout for threads above core size
                new ArrayBlockingQueue<>(2),                 // work queue - holds tasks once core threads are busy
                new ThreadPoolExecutor.CallerRunsPolicy()    // what to do once BOTH queue and max threads are full
        );

        for (int i = 1; i <= 10; i++) {
            int taskId = i;
            executor.execute(() -> {
                System.out.println("Task " + taskId + " running on " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
    }
}
