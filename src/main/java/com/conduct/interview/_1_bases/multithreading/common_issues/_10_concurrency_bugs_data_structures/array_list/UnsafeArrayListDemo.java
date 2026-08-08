package com.conduct.interview._1_bases.multithreading.common_issues._10_concurrency_bugs_data_structures.array_list;

import java.util.ArrayList;
import java.util.List;

/**
 * BAD PRACTICE: plain ArrayList is not thread-safe. add() is not atomic
 * (check size, grow if needed, then set element), so concurrent writes
 * corrupt it - elements get lost or an ArrayIndexOutOfBoundsException is thrown.
 */
public class UnsafeArrayListDemo {

    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = new ArrayList<>();

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
        System.out.println("Actual size:   " + list.size()); // often less, or throws
    }
}
