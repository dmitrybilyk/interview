package com.conduct.interview._1_bases.multithreading.java_concurrent_package.thread_factory;

import java.util.concurrent.ThreadFactory;

/**
 * ThreadFactory controls how new threads get created - here just to give them readable
 * names. In practice it's usually passed to an ExecutorService (e.g. Executors.newFixedThreadPool(n, factory))
 * so pool threads are named/configured instead of getting generic "pool-1-thread-1" names.
 */
public class ThreadFactoryDemo {

    public static void main(String[] args) {
        ThreadFactory namedFactory = runnable -> new Thread(runnable, "worker-thread");

        Thread thread = namedFactory.newThread(() ->
                System.out.println("Running on " + Thread.currentThread().getName()));

        thread.start();
    }
}
