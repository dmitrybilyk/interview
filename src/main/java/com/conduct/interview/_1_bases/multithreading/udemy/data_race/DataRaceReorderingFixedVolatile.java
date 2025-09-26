package com.conduct.interview._1_bases.multithreading.udemy.data_race;

public class DataRaceReorderingFixedVolatile {
//    static volatile int x = 0; // Prevent reordering and ensure visibility
    static  int x = 0;
//    static volatile int y = 0;
    static int y = 0;
    static int a = 0;
    static int b = 0;

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            x = 0;
            y = 0;
            a = 0;
            b = 0;

            Thread t1 = new Thread(() -> {
                methodOne();
            });

            Thread t2 = new Thread(() -> {
                methodTwo();
            });

            t1.start();
            t2.start();

            t1.join();
            t2.join();

            if (a == 0 && b == 0) {
                System.out.println("Reordering detected: a = " + a + ", b = " + b);
            }
        }
    }

    static void methodOne() {
        x = 1; // Write x
        a = y; // Read y
    }

    static void methodTwo() {
        y = 1; // Write y
        b = x; // Read x
    }
}