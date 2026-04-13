package com.conduct.interview._1_bases.multithreading.common_issues.data_structures.set;

import lombok.SneakyThrows;

import java.util.HashSet;
import java.util.Set;

public class UnsafeSetExample {

    @SneakyThrows
    public static void main(String[] args) {
        Set<Integer> set = new HashSet<>();

        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
                set.add(i);
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Size: " + set.size()); // ❌ may be WRONG
    }
}