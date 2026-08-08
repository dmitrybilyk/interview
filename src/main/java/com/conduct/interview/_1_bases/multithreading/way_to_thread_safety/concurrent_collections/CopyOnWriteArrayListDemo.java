package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.concurrent_collections;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CopyOnWriteArrayList copies the whole backing array on every write, so readers never see a
 * half-modified array and never need locking. Good for read-heavy, write-rare cases (writes are
 * expensive here - every add() copies the entire array).
 */
public class CopyOnWriteArrayListDemo {

    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = new CopyOnWriteArrayList<>();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                list.add(i);
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Expected size: 2000");
        System.out.println("Actual size:   " + list.size()); // always correct
    }
}
