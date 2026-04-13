package com.conduct.interview._1_bases.multithreading.common_issues.deadlock.check;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DeadlockWithOrderReentrantLock {
    static Lock lock = new ReentrantLock();
    static Lock lock2 = new ReentrantLock();
    public static void main(String[] args) {
        A a = new A();
        B b = new B();

        Thread thread = new Thread(() -> {
            lock.lock();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            lock2.lock();
        });

        Thread thread1 = new Thread(() -> {
            lock2.lock();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            lock.lock();
        });

        thread.start();
        thread1.start();
    }
}
