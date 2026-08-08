package com.conduct.interview._1_bases.multithreading.producer_consumer.with_reentrant_lock;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Same idea as wait/notify, but with an explicit Lock and Condition instead of the implicit
 * monitor - notFull.await()/signal() replace wait()/notifyAll(), with clearer intent since
 * producer and consumer each get their own condition to wait on.
 */
public class ReentrantLockDemo {

    private static final int CAPACITY = 3;
    private static final int ITEMS_TO_PRODUCE = 5;

    public static void main(String[] args) {
        Queue<Integer> queue = new LinkedList<>();
        Lock lock = new ReentrantLock();
        Condition notFull = lock.newCondition();
        Condition notEmpty = lock.newCondition();

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= ITEMS_TO_PRODUCE; i++) {
                lock.lock();
                try {
                    while (queue.size() == CAPACITY) {
                        System.out.println("Producer: queue full, waiting...");
                        notFull.await();
                    }
                    queue.add(i);
                    System.out.println("Produced " + i);
                    notEmpty.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        }, "Producer");

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= ITEMS_TO_PRODUCE; i++) {
                lock.lock();
                try {
                    while (queue.isEmpty()) {
                        System.out.println("Consumer: queue empty, waiting...");
                        notEmpty.await();
                    }
                    System.out.println("Consumed " + queue.poll());
                    notFull.signal();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            }
        }, "Consumer");

        producer.start();
        consumer.start();
    }
}
