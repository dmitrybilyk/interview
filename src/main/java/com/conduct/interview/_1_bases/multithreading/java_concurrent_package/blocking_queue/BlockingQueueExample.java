package com.conduct.interview._1_bases.multithreading.java_concurrent_package.blocking_queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * put() blocks the producer if the queue is full, take() blocks the consumer if it's empty -
 * no manual wait/notify needed to coordinate the two threads.
 */
public class BlockingQueueExample {

    public static void main(String[] args) {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(2);

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                try {
                    queue.put("message-" + i);
                    System.out.println("Produced message-" + i);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                try {
                    String message = queue.take();
                    System.out.println("Consumed " + message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        producer.start();
        consumer.start();
    }
}
