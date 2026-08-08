package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ExecutorService manages a pool of threads for you - submit tasks to it instead of
 * creating raw threads. Always shutdown() it when done, or its threads leak.
 */
public class ExecutorServiceDemo {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> System.out.println("Task 1 on " + Thread.currentThread().getName()));
        executor.submit(() -> System.out.println("Task 2 on " + Thread.currentThread().getName()));

        executor.shutdown();
    }
}
