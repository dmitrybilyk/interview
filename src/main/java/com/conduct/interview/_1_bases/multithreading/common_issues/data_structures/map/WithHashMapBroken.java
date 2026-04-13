package com.conduct.interview._1_bases.multithreading.common_issues.data_structures.map;

import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;

public class WithHashMapBroken {

    @SneakyThrows
    public static void main(String[] args) {
        Map<Integer, Integer> map = new HashMap<>();

        Runnable task = () -> {
            for (int i = 0; i < 10000; i++) {
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
        System.out.println("Actual size:   " + map.size());
    }
}