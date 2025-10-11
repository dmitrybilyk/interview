package com.conduct.interview._1_bases.multithreading.udemy.atomic_references;

import java.util.concurrent.atomic.AtomicReference;

public class WithAtomic {
    private static AtomicReference<String> sharedData = new AtomicReference<>("Start");

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> updateData("T1"));
        Thread t2 = new Thread(() -> updateData("T2"));

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final value: " + sharedData.get());
    }

    private static void updateData(String newValue) {
        for (int i = 0; i < 1000; i++) {
            sharedData.updateAndGet(old -> old + " -> " + newValue);
        }
    }
}
