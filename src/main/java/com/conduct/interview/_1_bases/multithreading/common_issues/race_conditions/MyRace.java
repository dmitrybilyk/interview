package com.conduct.interview._1_bases.multithreading.common_issues.race_conditions;

import java.util.concurrent.locks.ReentrantLock;

public class MyRace {
    public static void main(String[] args) throws InterruptedException {
        CommonResource commonResource = new CommonResource();
        Thread thread1 = new Thread(new MyRunnable(commonResource));
        Thread thread2 = new Thread(new MyRunnable(commonResource));
        thread1.start();
        thread2.start();
        thread2.join();
        thread1.join();
        System.out.println(commonResource.getFlag());
        System.out.println(commonResource.getCount());
    }


}

class MyRunnable implements Runnable {
    private CommonResource commonResource;

    public MyRunnable(CommonResource commonResource) {
        this.commonResource = commonResource;
    }

    public void run() {
        for (int i = 0; i < 1000; i++) {
            commonResource.increment();
        }
    }
}

class CommonResource {
    private ReentrantLock lock = new ReentrantLock();
    private boolean flag;
    private int count;

    public void increment() {
        lock.lock();
        try {
        boolean current = flag;
        flag = !current;
        count++;
        } finally {
            lock.unlock();
        }
    }

    public int getCount() {
        return count;
    }
    public boolean getFlag() {
        return flag;
    }
}