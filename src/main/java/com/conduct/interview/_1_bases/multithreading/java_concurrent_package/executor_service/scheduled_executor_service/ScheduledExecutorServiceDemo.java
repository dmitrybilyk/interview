package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.scheduled_executor_service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * scheduleAtFixedRate runs the task every N seconds, measured from the start of each run -
 * if a run takes longer than the period, the next one starts right after it, no overlap.
 */
public class ScheduledExecutorServiceDemo {

    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        Runnable task = () -> System.out.println("Tick at " + System.currentTimeMillis());

        // run once after a 1s initial delay, then every 2s
        executor.scheduleAtFixedRate(task, 1, 2, TimeUnit.SECONDS);
    }
}
