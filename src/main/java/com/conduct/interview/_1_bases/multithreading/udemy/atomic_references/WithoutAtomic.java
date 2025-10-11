package com.conduct.interview._1_bases.multithreading.udemy.atomic_references;

public class WithoutAtomic {
    private static String sharedData = "Start";

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> updateData("T1"));
        Thread t2 = new Thread(() -> updateData("T2"));

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Final value: " + sharedData);
    }

    private static void updateData(String newValue) {
        for (int i = 0; i < 1000; i++) {
            String old = sharedData;               // read
            String updated = old + " -> " + newValue; // modify
            sharedData = updated;                  // write
        }
    }
}
