package com.conduct.interview._1_bases.multithreading.udemy.reentrant_locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadWriteLockExample {
    public static void main(String[] args) throws InterruptedException {
        SharedCounter counter = new SharedCounter();
        ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true); // fair lock
        Lock readLock = rwLock.readLock();
        Lock writeLock = rwLock.writeLock();

        Thread writer1 = new Thread(new CounterWriter(counter, writeLock), "Writer-1");
        Thread writer2 = new Thread(new CounterWriter(counter, writeLock), "Writer-2");

        Thread reader1 = new Thread(new CounterReader(counter, readLock), "Reader-1");
        Thread reader2 = new Thread(new CounterReader(counter, readLock), "Reader-2");

        writer1.start();
        writer2.start();
        reader1.start();
        reader2.start();

        writer1.join();
        writer2.join();
        reader1.join();
        reader2.join();

        System.out.println("Final counter value: " + counter.getValue());
    }
}

class SharedCounter {
    private int value = 0;

    public void increment() {
        value++;
        System.out.println(Thread.currentThread().getName() + " incremented to " + value);
    }

    public int getValue() {
        return value;
    }
}

// Writer: increments the counter using write lock
class CounterWriter implements Runnable {
    private final SharedCounter counter;
    private final Lock writeLock;

    public CounterWriter(SharedCounter counter, Lock writeLock) {
        this.counter = counter;
        this.writeLock = writeLock;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            writeLock.lock();
            try {
                counter.increment();
                // simulate some work
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                writeLock.unlock();
            }
        }
    }
}

// Reader: reads the counter using read lock
class CounterReader implements Runnable {
    private final SharedCounter counter;
    private final Lock readLock;

    public CounterReader(SharedCounter counter, Lock readLock) {
        this.counter = counter;
        this.readLock = readLock;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            readLock.lock();
            try {
                int val = counter.getValue();
                System.out.println(Thread.currentThread().getName() + " read value: " + val);
                // simulate some work
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                readLock.unlock();
            }
        }
    }
}
