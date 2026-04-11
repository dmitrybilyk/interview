package com.conduct.interview._1_bases.multithreading.common_issues.deadlock.check;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FixedDeadlockWithOrderReentrantLock {
    static Lock lock = new ReentrantLock();
    static Lock lock2 = new ReentrantLock();
    public static void main(String[] args) {
        A a = new A();
        B b = new B();

        Thread thread = new Thread(() -> {
            try {
                lock.tryLock(100, TimeUnit.MICROSECONDS);try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                lock2.tryLock(100, TimeUnit.MICROSECONDS);try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } catch (InterruptedException e) {
                lock.unlock();
                lock2.unlock();
            }
        });


        Thread thread1 = new Thread(() -> {
            try {
                lock2.tryLock(100, TimeUnit.MICROSECONDS);try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                lock.tryLock(100, TimeUnit.MICROSECONDS);try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            } catch (InterruptedException e) {
                lock.unlock();
                lock2.unlock();
            }
        });

        thread.start();
        thread1.start();
    }
}
