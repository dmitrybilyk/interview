package com.conduct.interview._1_bases.multithreading.udemy;

public class DataRaceDemo {
    static class SharedData {
        int x = 0;
        int y = 0;
    }

    public static void main(String[] args) {
        SharedData data = new SharedData();

        Thread t1 = new Thread(() -> {
            data.x = 1;
            data.y = 1;
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            if (data.y == 1) {
                System.out.println(Thread.currentThread().getName() + ": x = " + data.x);
            }
        }, "Thread-2");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Main interrupted");
        }
    }
}