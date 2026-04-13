package com.conduct.interview._1_bases.multithreading.common_issues.data_structures.set;

import lombok.SneakyThrows;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SafeSetExample {

    @SneakyThrows
    public static void main(String[] args) {
        Set<Integer> set = ConcurrentHashMap.newKeySet();

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

        System.out.println("Size: " + set.size()); // ✅ ALWAYS correct
    }
}