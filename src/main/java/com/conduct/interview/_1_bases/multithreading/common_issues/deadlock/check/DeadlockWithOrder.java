package com.conduct.interview._1_bases.multithreading.common_issues.deadlock.check;

public class DeadlockWithOrder {
    public static void main(String[] args) {
        A a = new A();
        B b = new B();

        Thread thread = new Thread(() -> {
            synchronized (a) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (b) {
                    System.out.println("working in b lock");
                }
            }
        });

        Thread thread1 = new Thread(() ->
        {
            synchronized (b) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (a) {
                    System.out.println("working in a lock");
                }
            }
        });

        thread.start();
        thread1.start();
    }
}

class A {}
class B {}

