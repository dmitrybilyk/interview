package com.conduct.interview._1_bases.multithreading.producer_consumer.wait_notify;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Classic producer-consumer with wait()/notifyAll() on a shared, bounded queue:
 * producer waits when the queue is full, consumer waits when it's empty.
 */
public class WaitNotifyDemo {

    private static final int CAPACITY = 3;
    private static final int ITEMS_TO_PRODUCE = 5;

    public static void main(String[] args) {
        Queue<Integer> queue = new LinkedList<>();

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= ITEMS_TO_PRODUCE; i++) {
                synchronized (queue) {
                    while (queue.size() == CAPACITY) {
                        wait(queue, "Producer: queue full, waiting...");
                    }
                    queue.add(i);
                    System.out.println("Produced " + i);
                    queue.notifyAll();
                }
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= ITEMS_TO_PRODUCE; i++) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        wait(queue, "Consumer: queue empty, waiting...");
                    }
                    System.out.println("Consumed " + queue.poll());
                    queue.notifyAll();
                }
            }
        }, "Consumer");

        producer.start();
        consumer.start();
    }

    private static void wait(Object lock, String message) {
        System.out.println(message);
        try {
            lock.wait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
