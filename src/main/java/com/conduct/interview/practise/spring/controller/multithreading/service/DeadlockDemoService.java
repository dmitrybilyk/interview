package com.conduct.interview.practise.spring.controller.multithreading.service;

import org.springframework.stereotype.Service;

import static java.lang.Thread.sleep;

@Service
public class DeadlockDemoService {

    private final Object objA = new Object();
    private final Object objB = new Object();

    public void startTask() {
        System.out.println("Starting potential deadlock...");

        Thread t1 = new Thread(() -> {
            synchronized (objA) {
                System.out.println(Thread.currentThread().getName() + " locked objA");
                try {
                    sleep(1000); // <--- ensures t2 locks objB first
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println(Thread.currentThread().getName() + " waiting for objB...");
                synchronized (objB) {
                    System.out.println(Thread.currentThread().getName() + " locked objB");
                }
            }
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            synchronized (objB) {
                System.out.println(Thread.currentThread().getName() + " locked objB");
                try {
                    sleep(1000); // <--- ensures t1 locks objA first
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println(Thread.currentThread().getName() + " waiting for objA...");
                synchronized (objA) {
                    System.out.println(Thread.currentThread().getName() + " locked objA");
                }
            }
        }, "Thread-2");

        t1.start();
        t2.start();
    }
}
