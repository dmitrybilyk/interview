package com.conduct.interview._1_bases.multithreading.udemy.wait_notify;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockConditionExample {

    private static final int CAPACITY = 5;
    private static final Queue<String> queue = new LinkedList<>();

    // Shared lock
    private static final Lock lock = new ReentrantLock();

    // Two conditions: one for "not full", one for "not empty"
    private static final Condition notFull = lock.newCondition();
    private static final Condition notEmpty = lock.newCondition();

    public static void main(String[] args) {

        Thread producer = new Thread(() -> {
            int count = 1;
            try {
                while (true) {
                    produce("item-" + count++);
                    Thread.sleep(500); // simulate work
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    consume();
                    Thread.sleep(1000); // simulate work
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
    }

    private static void produce(String item) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == CAPACITY) {
                System.out.println("Queue full. Producer waiting...");
                notFull.await();  // wait until space is available
            }

            queue.add(item);
            System.out.println("Produced: " + item + " | Size: " + queue.size());

            // Signal that there’s something to consume
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    private static void consume() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) {
                System.out.println("Queue empty. Consumer waiting...");
                notEmpty.await(); // wait until there’s an item
            }

            String item = queue.poll();
            System.out.println("Consumed: " + item + " | Size: " + queue.size());

            // Signal that there’s space to produce
            notFull.signal();
        } finally {
            lock.unlock();
        }
    }
}
