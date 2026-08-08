package com.conduct.interview._1_bases.multithreading.common_issues._10_concurrency_bugs_data_structures.set;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FIX: ConcurrentHashMap.newKeySet() is thread-safe, so adds from both threads are never lost.
 */
public class SafeConcurrentHashSetDemo {

    public static void main(String[] args) throws InterruptedException {
        Set<Integer> set = ConcurrentHashMap.newKeySet();

        Runnable task = () -> {
            for (int i = 0; i < 10_000; i++) {
                set.add(i);
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Expected size: 10000");
        System.out.println("Actual size:   " + set.size()); // always correct
    }
}
