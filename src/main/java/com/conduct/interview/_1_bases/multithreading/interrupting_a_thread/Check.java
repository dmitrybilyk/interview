package com.conduct.interview._1_bases.multithreading.interrupting_a_thread;

public class Check {

    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                System.out.println("Doing something");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
                System.out.println("Doing something additionally");
            }

            System.out.println("Quiting the thread");
        });

        worker.start();
        Thread.sleep(2000);
        worker.interrupt();
    }

}
