package com.conduct.interview._1_bases.multithreading.producer_consumer.wait_notify;

import java.util.LinkedList;
import java.util.Queue;

public class WaitNotifyCheck {
    public static void main(String[] args) {
        Queue<Integer> queue = new LinkedList<>();
        int CAPACITY = 3;
        int MAX_ITEMS = 5;

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= MAX_ITEMS; i++) {
                synchronized (queue) {
                    while(queue.size() == CAPACITY) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    queue.add(i);
                    queue.notifyAll();
                }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= MAX_ITEMS; i++) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println(queue.poll());
                    queue.notifyAll();
                }
            }
        });

        producer.start();
        consumer.start();

    }
}
