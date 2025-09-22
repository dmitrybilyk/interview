package com.conduct.interview._1_bases.multithreading.udemy.interrupt_thread;

class ComputingThread extends Thread {
    @Override
    public void run() {
        long sum = 0;
        long sum2= 10;
        for (long i = 0; i < Long.MAX_VALUE; i++) {
            // Periodically check interrupt flag
            if (Thread.currentThread().isInterrupted()) {
                System.out.println("Thread interrupted during computation at i = " + i);
                break;
            }
            sum += i; // simulate heavy computation
            sum2 += i * 100;
        }
        System.out.println("Thread exiting cleanly");
    }
}

public class InterruptExample2 {
    public static void main(String[] args) throws InterruptedException {
        ComputingThread t = new ComputingThread();
        t.start();
        Thread.sleep(100);
        System.out.println("Interrupting...");
        t.interrupt(); // sets interrupt flag
    }
}
