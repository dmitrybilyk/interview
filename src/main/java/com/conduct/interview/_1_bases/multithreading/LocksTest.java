package com.conduct.interview._1_bases.multithreading;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class LocksTest {
    public static void main(String[] args) throws InterruptedException {
        SomeClass someClass = new SomeClass();
        Runnable runnable = () -> {
//            for (int i = 0; i < 1000; i++) {
                try {
                    someClass.increment();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
//            }
        };
        Thread thread = new Thread(runnable);
        Thread thread2 = new Thread(runnable);

        thread.start();
        thread2.start();

        thread.join();
        thread2.join();

        System.out.println(someClass.getCounter());
    }
}

class SomeClass {
    private final ReentrantLock lock = new ReentrantLock();
    private int counter;

    public int getCounter() {
        return counter;
    }

    public void increment() throws InterruptedException {
            if (lock.tryLock(2, TimeUnit.SECONDS)) {
                try {
                    System.out.println("Started synced block - " + Thread.currentThread().getName());
                    Thread.sleep(5000);
                    System.out.println("Completed synced block" + Thread.currentThread().getName());
                } finally {
                    lock.unlock();
                }
            }


//        synchronized (this) {
//            System.out.println("Started synced block");
//            Thread.sleep(5000);
//            System.out.println("Completed synced block");
//        }
        this.counter++;
    }
}
