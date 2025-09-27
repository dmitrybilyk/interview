package com.conduct.interview._1_bases.multithreading.udemy;

public class MinimalDeadlockDemo {
    public static void main(String[] args) {
        String friend1 = "Alice";
        String friend2 = "Bob";

        Thread t1 = new Thread(() -> greet(friend1, friend2), "Thread-1");
        Thread t2 = new Thread(() -> greet(friend2, friend1), "Thread-2");

        t1.start();
        t2.start();

//        try {
//            t1.join(2000);
//            t2.join(2000);
//            if (t1.isAlive() || t2.isAlive()) {
//                System.out.println("Deadlock detected");
//                t1.interrupt();
//                t2.interrupt();
//            }
//        } catch (InterruptedException e) {
//            System.out.println("Main interrupted");
//        }
    }

    static void greet(String me, String other) {
        synchronized (me) {
            try { Thread.sleep(100); } catch (InterruptedException e) { return; }
            synchronized (other) {
                System.out.println(Thread.currentThread().getName() + ": " + me + " greets " + other);
            }
        }
    }
}