package com.conduct.interview.practise.spring.controller.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.sleep;

@Service
public class DeadlockLockDemoService {

    private final Lock lockA = new ReentrantLock();
    private final Lock lockB = new ReentrantLock();

    public void startTask() {
        System.out.println("Starting potential ReentrantLock deadlock...");

        Thread t1 = new Thread(() -> {
            lockA.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " locked A");
                sleep(1000); // ensure t2 gets B first
                System.out.println(Thread.currentThread().getName() + " waiting for B...");
                lockB.lock(); // <- waits forever if t2 holds it
                try {
                    System.out.println(Thread.currentThread().getName() + " locked B");
                } finally {
                    lockB.unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lockA.unlock();
            }
        }, "Thread-1");

        Thread t2 = new Thread(() -> {
            lockB.lock();
            try {
                System.out.println(Thread.currentThread().getName() + " locked B");
                sleep(1000); // ensure t1 gets A first
                System.out.println(Thread.currentThread().getName() + " waiting for A...");
                lockA.lock(); // <- waits forever if t1 holds it
                try {
                    System.out.println(Thread.currentThread().getName() + " locked A");
                } finally {
                    lockA.unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lockB.unlock();
            }
        }, "Thread-2");

        t1.start();
        t2.start();
    }
}
