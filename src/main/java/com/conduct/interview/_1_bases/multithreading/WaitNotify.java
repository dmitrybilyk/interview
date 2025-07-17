package com.conduct.interview._1_bases.multithreading;

public class WaitNotify {
    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("Thread 1 waiting...");
                    lock.wait(); // Correct: called on lock object inside synchronized
                    System.out.println("Thread 1 resumed!");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Thread 2 notifying...");
                lock.notify(); // Correct: called on lock object inside synchronized
            }
        });

        t1.start();
        Thread.sleep(1000); // Give t1 time to wait
        t2.start();
    }
}
