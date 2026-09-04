package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.synchronized_keyword;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * synchronized on an instance method locks on "this" - only one thread can be inside
 * calculate() for a given SynchronizedMethods instance at a time.
 */
public class SynchronizedMethods {

    private int sum = 0;

    public synchronized void calculate() {
        sum++;
    }

    public int getSum() {
        return sum;
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        SynchronizedMethods summation = new SynchronizedMethods();

        for (int i = 0; i < 1000; i++) {
            executor.submit(summation::calculate);
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.SECONDS);

        System.out.println("Expected: 1000");
        System.out.println("Actual:   " + summation.getSum());
    }
}
