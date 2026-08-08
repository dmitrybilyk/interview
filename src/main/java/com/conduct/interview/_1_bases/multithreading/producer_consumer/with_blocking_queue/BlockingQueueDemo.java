package com.conduct.interview._1_bases.multithreading.producer_consumer.with_blocking_queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Simplest way to solve producer-consumer: BlockingQueue already handles all the
 * waiting/notifying internally - put() blocks when full, take() blocks when empty.
 */
public class BlockingQueueDemo {

    private static final int ITEMS_TO_PRODUCE = 5;

    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(3);

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= ITEMS_TO_PRODUCE; i++) {
                try {
                    queue.put(i);
                    System.out.println("Produced " + i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= ITEMS_TO_PRODUCE; i++) {
                try {
                    System.out.println("Consumed " + queue.take());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "Consumer");

        producer.start();
        consumer.start();
    }
}
