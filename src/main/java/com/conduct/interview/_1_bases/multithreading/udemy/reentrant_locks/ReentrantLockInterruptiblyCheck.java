package com.conduct.interview._1_bases.multithreading.udemy.reentrant_locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockInterruptiblyCheck {
    public static void main(String[] args) throws InterruptedException {
        ClassWithSharedParameter2 shared = new ClassWithSharedParameter2();
        Lock lock = new ReentrantLock(true); // fair lock

        Thread thread1 = new Thread(new Incrementor2(shared, lock), "Thread-1");
        Thread thread2 = new Thread(new Incrementor2(shared, lock), "Thread-2");

        thread1.start();
        thread2.start();

        // Interrupt thread2 after a short delay to demonstrate interruptible locking
        Thread.sleep(100);
        thread2.interrupt();

        thread1.join();
        thread2.join();

        System.out.println("Final value: " + shared.getValue());
    }
}

class ClassWithSharedParameter2 {
    private int value;

    public void increment() {
        System.out.println(Thread.currentThread().getName() + " is incrementing");
        value++;
    }

    public int getValue() {
        return value;
    }
}

class Incrementor2 implements Runnable {
    private final ClassWithSharedParameter2 shared;
    private final Lock lock;

    public Incrementor2(ClassWithSharedParameter2 shared, Lock lock) {
        this.shared = shared;
        this.lock = lock;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            try {
                // Try to acquire the lock but respond to interruptions
                lock.lockInterruptibly();
                try {
                    shared.increment();
                    // Optional: simulate work
                    Thread.sleep(1);
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " was interrupted while waiting for the lock");
                Thread.currentThread().interrupt(); // restore interrupted status
                break; // exit the loop
            }
        }
    }
}
