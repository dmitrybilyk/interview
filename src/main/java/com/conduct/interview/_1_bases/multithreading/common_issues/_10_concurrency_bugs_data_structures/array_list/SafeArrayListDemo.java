package com.conduct.interview._1_bases.multithreading.common_issues._10_concurrency_bugs_data_structures.array_list;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * FIX: CopyOnWriteArrayList is thread-safe, so writes from both threads are never lost.
 */
public class SafeArrayListDemo {

    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = new CopyOnWriteArrayList<>();

        Runnable task = () -> {
            for (int i = 0; i < 10_000; i++) {
                list.add(i);
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Expected size: 20000");
        System.out.println("Actual size:   " + list.size()); // always correct
    }
}
