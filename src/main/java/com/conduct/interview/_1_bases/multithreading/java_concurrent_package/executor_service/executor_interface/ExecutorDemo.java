package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.executor_interface;

import java.util.concurrent.Executor;

/**
 * Executor is the base interface: just one method, execute(Runnable) - fire and forget,
 * no result, no shutdown, no pooling built in. ExecutorService extends it and adds all of that.
 */
public class ExecutorDemo {

    public static void main(String[] args) {
        Executor executor = runnable -> new Thread(runnable).start();
        executor.execute(() -> System.out.println("Running on " + Thread.currentThread().getName()));
    }
}
