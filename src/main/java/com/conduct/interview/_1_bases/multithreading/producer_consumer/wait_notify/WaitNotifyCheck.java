package com.conduct.interview._1_bases.multithreading.producer_consumer.wait_notify;// File: WaitNotifyCheck.java

public class WaitNotifyCheck {

    private static final Object lock = new Object();
    private static boolean ready = false;

    public static void main(String[] args) {
        Thread waitingThread = new Thread(() -> {
            synchronized (lock) {
                System.out.println(Thread.currentThread().getName() + ": Waiting for signal...");
                while (!ready) {
                    try {
                        lock.wait(); // release lock and wait
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println(Thread.currentThread().getName() + ": Interrupted!");
                    }
                }
                System.out.println(Thread.currentThread().getName() + ": Received signal, proceeding...");
            }
        }, "WaitingThread");

        Thread notifyingThread = new Thread(() -> {
            try {
                Thread.sleep(2000); // simulate some work before notifying
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            synchronized (lock) {
                ready = true;
                System.out.println(Thread.currentThread().getName() + ": Sending signal...");
                lock.notify(); // wake up waiting thread
            }
        }, "NotifyingThread");

        waitingThread.start();
        notifyingThread.start();
    }
}
