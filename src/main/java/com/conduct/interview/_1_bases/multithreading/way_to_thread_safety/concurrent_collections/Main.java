package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.concurrent_collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Collection<Integer> noneSyncCollection = new ArrayList<>();
        CopyOnWriteArrayList<Integer> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        Collection<Integer> syncCollection = Collections.synchronizedCollection(new ArrayList<>());
        Runnable listOperations = () -> {
            copyOnWriteArrayList.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));
        };

        Thread thread1 = new Thread(listOperations);
        Thread thread2 = new Thread(listOperations);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(copyOnWriteArrayList.size());
    }
}
