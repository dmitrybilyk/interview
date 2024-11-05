package com.conduct.interview._1_bases.multithreading.common_problems.race_conditions;

class Counter {
    private int count = 0;

    public synchronized void increment() {
        count++; // This is not atomic and can cause race conditions
    }

    public int getCount() {
        return count;
    }
}

public class RaceConditionExample {
    public static void main(String[] args) {
        Counter counter = new Counter();

        // Create two threads that increment the counter 1000 times each
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        });

        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        });

        // Start both threads
        thread1.start();
        thread2.start();

        // Wait for both threads to finish
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Expected output is 2000, but due to race condition it may be less
        System.out.println("Final count: " + counter.getCount());
    }
}