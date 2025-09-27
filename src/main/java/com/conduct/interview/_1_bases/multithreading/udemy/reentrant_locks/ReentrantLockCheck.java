package com.conduct.interview._1_bases.multithreading.udemy.reentrant_locks;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockCheck {
    public static void main(String[] args) throws InterruptedException {
        ClassWithSharedParameter classWithSharedParameter = new ClassWithSharedParameter();
        Lock lock = new ReentrantLock(true);

        Thread thread1 = new Thread(new Incrementor(classWithSharedParameter, lock));
        Thread thread2 = new Thread(new Incrementor(classWithSharedParameter, lock));
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(classWithSharedParameter.getValue());
    }
}

class ClassWithSharedParameter {
    private int value;

    public void increment() {
        System.out.println(Thread.currentThread().getName() + " is incrementing");
        value++;
    }

    public int getValue() {
        return value;
    }
}

class Incrementor implements Runnable {
    private ClassWithSharedParameter classWithSharedParameter;
    private Lock lock;

    public Incrementor(ClassWithSharedParameter classWithSharedParameter,
                       Lock lock) {
        this.classWithSharedParameter = classWithSharedParameter;
        this.lock = lock;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10000; i++)
            if (lock.tryLock()) {
                try {
                    classWithSharedParameter.increment();
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println("Couldn't get lock, will try next time");
            }
    }
}
