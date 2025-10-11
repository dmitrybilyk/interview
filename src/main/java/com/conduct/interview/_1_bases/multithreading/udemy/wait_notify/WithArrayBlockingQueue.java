package com.conduct.interview._1_bases.multithreading.udemy.wait_notify;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import static java.lang.Thread.sleep;

public class WithArrayBlockingQueue {
    public static void main(String[] args) {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<>(5);
        Thread thread1 = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("adding item");
                    sleep(1000);
                    queue.put("item");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread thread2 = new Thread(() -> {
            while (true) {
                try {
                    System.out.println("processing " + queue.take());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread1.start();
        thread2.start();
    }
}
