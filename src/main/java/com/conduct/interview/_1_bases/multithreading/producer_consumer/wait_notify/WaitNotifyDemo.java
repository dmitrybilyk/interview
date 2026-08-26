package com.conduct.interview._1_bases.multithreading.producer_consumer.wait_notify;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Producer-Consumer using wait() / notifyAll().
 *
 * THE IDEA:
 *   - One thread produces items into a bounded queue.
 *   - Another thread consumes them.
 *   - If queue is FULL  → producer sleeps, consumer wakes it up after taking one.
 *   - If queue is EMPTY → consumer sleeps, producer wakes it up after adding one.
 *
 * 3 RULES TO REMEMBER:
 *
 *   1. wait() / notifyAll() must be inside synchronized(lock) — same lock, both threads.
 *
 *   2. wait() does two things: releases the lock AND puts thread to sleep.
 *      The other thread can now enter its synchronized block.
 *
 *   3. Always use WHILE, not IF, around wait().
 *      Reason: after waking up things may have changed — check again before acting.
 *      Example: 2 consumers both wake up, one takes the item first — the other
 *      must re-check or it will poll() from an empty queue and get null.
 */
public class WaitNotifyDemo {

    private static final int CAPACITY         = 3;
    private static final int ITEMS_TO_PRODUCE = 7;
    private static final int PRODUCER_DELAY   = 200;
    private static final int CONSUMER_DELAY   = 500; // consumer is slower → forces producer to wait

    public static void main(String[] args) throws InterruptedException {
        Queue<Integer> queue = new LinkedList<>();

        log("main", "start — capacity=" + CAPACITY + ", items=" + ITEMS_TO_PRODUCE);

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= ITEMS_TO_PRODUCE; i++) {

                synchronized (queue) {

                    while (queue.size() == CAPACITY) {          // queue full — go to sleep
                        log("Producer", "FULL " + queue + " — sleeping");
                        waitOn(queue);                          // releases lock, sleeps
                        log("Producer", "woke up — checking again");
                    }

                    queue.add(i);
                    log("Producer", "added " + i + " → " + queue);
                    queue.notifyAll();                          // wake up sleeping consumer
                }

                sleep(PRODUCER_DELAY);
            }
            log("Producer", "done");
        }, "Producer");

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= ITEMS_TO_PRODUCE; i++) {

                synchronized (queue) {

                    while (queue.isEmpty()) {                   // queue empty — go to sleep
                        log("Consumer", "EMPTY — sleeping");
                        waitOn(queue);                          // releases lock, sleeps
                        log("Consumer", "woke up — checking again");
                    }

                    int item = queue.poll();
                    log("Consumer", "took " + item + " → " + queue);
                    queue.notifyAll();                          // wake up sleeping producer
                }

                sleep(CONSUMER_DELAY);
            }
            log("Consumer", "done");
        }, "Consumer");

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
        log("main", "finished");
    }

    private static void waitOn(Object lock) {
        try {
            lock.wait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void log(String who, String msg) {
        System.out.printf("[%-8s] %s%n", who, msg);
    }
}
