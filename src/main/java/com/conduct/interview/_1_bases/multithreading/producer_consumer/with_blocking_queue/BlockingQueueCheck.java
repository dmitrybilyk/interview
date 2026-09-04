package com.conduct.interview._1_bases.multithreading.producer_consumer.with_blocking_queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueCheck {
    public static void main(String[] args) {
        int MAX_ITEMS = 5;
        int CAPACITY = 3;
        BlockingQueue<Integer> blockingQueue = new ArrayBlockingQueue<>(CAPACITY);

        Thread producer = new Thread() {
            @Override
            public void run() {
                for (int i = 1; i <= MAX_ITEMS; i++) {
                    try {
                        blockingQueue.put(i);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };

        Thread consumer = new Thread() {
            @Override
            public void run() {
                for (int i = 1; i <= MAX_ITEMS; i++) {
                    try {
                        System.out.println(blockingQueue.take());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };

        producer.start();
        consumer.start();
    }
}
