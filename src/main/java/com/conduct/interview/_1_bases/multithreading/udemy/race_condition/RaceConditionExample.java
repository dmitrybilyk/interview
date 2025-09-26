package com.conduct.interview._1_bases.multithreading.udemy.race_condition;

public class RaceConditionExample {
    private static int counter = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(RaceConditionExample::increment);
        Thread t2 = new Thread(RaceConditionExample::increment);

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Final counter = " + counter);
    }
    public class DataRaceXY {
        static int x = 0;
        static int y = 0;

        public static void main(String[] args) throws InterruptedException {
            for (int i = 0; i < 1_000_000; i++) {
                x = 0;
                y = 0;

                Thread t1 = new Thread(() -> {
                    x = 1;         // write
                    int r1 = y;    // read
                    if (r1 == 0) {
                        System.out.println("Thread1 saw y=0");
                    }
                });

                Thread t2 = new Thread(() -> {
                    y = 1;         // write
                    int r2 = x;    // read
                    if (r2 == 0) {
                        System.out.println("Thread2 saw x=0");
                    }
                });

                t1.start();
                t2.start();
                t1.join();
                t2.join();
            }
        }
    }

    private static void increment() {
        for (int i = 0; i < 1000000; i++) {
            synchronized (RaceConditionExample.class) {
                counter++; // <-- NOT atomic!
            }
        }
    }
}
