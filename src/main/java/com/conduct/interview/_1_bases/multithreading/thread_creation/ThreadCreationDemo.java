package com.conduct.interview._1_bases.multithreading.thread_creation;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Three ways to run code on a thread, in order of preference (bottom to top).
 */
public class ThreadCreationDemo {

    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("Running via extends Thread");
        }
    }

    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("Running via implements Runnable");
        }
    }

    static class MyCallable implements Callable<String> {
        @Override
        public String call() {
            return "Result via Callable";
        }
    }

    public static void main(String[] args) throws Exception {
        // 1. extends Thread - burns your one superclass slot (no multiple inheritance in Java)
        new MyThread().start();

        // 2. implements Runnable - preferred: no result, but the class is still free to extend something else
        new Thread(new MyRunnable()).start();

        // 3. Callable - like Runnable but returns a value and can throw checked exceptions;
        //    only usable through an ExecutorService, which hands back a Future
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new MyCallable());
        System.out.println(future.get());
        executor.shutdown();
    }
}
