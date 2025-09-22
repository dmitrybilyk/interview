package com.conduct.interview._1_bases.multithreading.udemy.interrupt_thread;

class SleepingThread extends Thread {
    @Override
    public void run() {
        try {
            System.out.println("Thread sleeping...");
            Thread.sleep(10000); // sleep 10s
            System.out.println("Woke up normally");
        } catch (InterruptedException e) {
            System.out.println("Thread was interrupted while sleeping");
        }
    }
}

public class InterruptExample {
    public static void main(String[] args) throws InterruptedException {
        SleepingThread t = new SleepingThread();
        t.start();
        Thread.sleep(2000); // give it time to start
        System.out.println("Interrupting...");
        t.interrupt(); // triggers InterruptedException
    }
}
