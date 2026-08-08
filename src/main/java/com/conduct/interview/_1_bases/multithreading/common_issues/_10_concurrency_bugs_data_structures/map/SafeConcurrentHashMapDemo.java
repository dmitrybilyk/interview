package com.conduct.interview._1_bases.multithreading.common_issues._10_concurrency_bugs_data_structures.map;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * FIX: ConcurrentHashMap is thread-safe, so writes from both threads are never lost.
 */
public class SafeConcurrentHashMapDemo {

    public static void main(String[] args) throws InterruptedException {
        Map<Integer, Integer> map = new ConcurrentHashMap<>();

        Runnable task = () -> {
            for (int i = 0; i < 10_000; i++) {
                map.put(i, i);
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Expected size: 10000");
        System.out.println("Actual size:   " + map.size()); // always correct
    }
}
