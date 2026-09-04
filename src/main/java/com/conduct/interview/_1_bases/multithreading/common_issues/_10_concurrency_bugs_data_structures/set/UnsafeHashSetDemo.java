package com.conduct.interview._1_bases.multithreading.common_issues._10_concurrency_bugs_data_structures.set;

import java.util.HashSet;
import java.util.Set;

/**
 * BAD PRACTICE: plain HashSet is not thread-safe, same problem as HashMap
 * (it's backed by one internally). Concurrent writes corrupt it and elements get lost.
 */
public class UnsafeHashSetDemo {

    public static void main(String[] args) throws InterruptedException {
        Set<Integer> set = new HashSet<>();

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
        System.out.println("Actual size:   " + set.size()); // often less - lost adds
    }
}
