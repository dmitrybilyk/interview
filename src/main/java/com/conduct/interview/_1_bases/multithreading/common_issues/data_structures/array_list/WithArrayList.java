package com.conduct.interview._1_bases.multithreading.common_issues.data_structures.array_list;

import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

public class WithArrayList {
    @SneakyThrows
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();

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

        System.out.println(list.size());
    }
}
