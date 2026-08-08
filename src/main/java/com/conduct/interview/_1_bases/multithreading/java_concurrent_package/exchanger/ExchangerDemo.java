package com.conduct.interview._1_bases.multithreading.java_concurrent_package.exchanger;

import java.util.concurrent.Exchanger;

/**
 * A rendezvous point for exactly two threads to swap values - both call exchange(),
 * each blocks until the other arrives, then each gets back what the other passed in.
 */
public class ExchangerDemo {

    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();

        new Thread(() -> {
            try {
                String received = exchanger.exchange("Data from Thread-A");
                System.out.println("Thread-A received: " + received);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Thread-A").start();

        new Thread(() -> {
            try {
                String received = exchanger.exchange("Data from Thread-B");
                System.out.println("Thread-B received: " + received);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Thread-B").start();
    }
}
