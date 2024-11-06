package com.conduct.interview._1_bases.multithreading.common_issues.deadlock;

public class DeadlockExample {
    static class A {}
    static class B {}

    static A objA = new A();
    static B objB = new B();

    public static void main(String[] args) {
        // Thread 1: Locks objA, then tries to lock objB
        Runnable task1 = () -> {
            synchronized (objA) {
                System.out.println("Thread 1: Holding lock on objA...");
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                
                System.out.println("Thread 1: Waiting for lock on objB...");
                synchronized (objB) {
                    System.out.println("Thread 1: Acquired lock on objB.");
                }
            }
        };

        // Thread 2: Locks objB, then tries to lock objA
        Runnable task2 = () -> {
            synchronized (objB) {
                System.out.println("Thread 2: Holding lock on objB...");
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                
                System.out.println("Thread 2: Waiting for lock on objA...");
                synchronized (objA) {
                    System.out.println("Thread 2: Acquired lock on objA.");
                }
            }
        };

        // Start both threads
        Thread thread1 = new Thread(task1);
        Thread thread2 = new Thread(task2);
        
        thread1.start();
        thread2.start();
    }
}
