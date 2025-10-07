package com.conduct.interview._1_bases.multithreading.producer_consumer.wait_notify;

public class WaitNotifyCheck2 {
    private static boolean ready = false;

    public static void main(String[] args) {

        Object lock = new Object();

        Thread waitThread = new Thread(() -> {
            System.out.println("waiting thread");

            synchronized (lock) {
                try {
                    while (!ready) {
                        lock.wait();
                    }
                    System.out.println("after wait");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
        }, "Waiting thread");
        waitThread.start();

        Thread notifyThread = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("notify thread");
            synchronized (lock) {
                ready = true;
                lock.notify();
            }
            System.out.println("after notify");
        }, "Notifying thread");
        notifyThread.start();

    }
}
