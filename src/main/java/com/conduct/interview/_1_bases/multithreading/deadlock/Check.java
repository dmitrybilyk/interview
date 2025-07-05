package com.conduct.interview._1_bases.multithreading.deadlock;

public class Check {
    static Object lock1 = new Object();
    static Object lock2 = new Object();
    public static void main(String[] args) {
        MyThread1 myThread1 = new MyThread1();
        MyThread2 myThread2 = new MyThread2();
        myThread1.start();
        myThread2.start();
        System.out.println("State of the thread 1 is " + myThread1.getState());
        System.out.println("State of the thread 2 is " + myThread2.getState());
        System.out.println("State of the thread 1 is " + myThread1.getState());
    }

    static class MyThread1 extends Thread {
        @Override
        public void run() {
            synchronized (lock1) {
                System.out.println("inside my thread1 in lock1");
                System.out.println(Thread.currentThread().getState());
                synchronized (lock2) {
                    System.out.println("inside my thread1 in lock2");
                    System.out.println(Thread.currentThread().getState());
                }
            }
            System.out.println("MyThread1 is running");
        }
    }
    static class MyThread2 extends Thread {
        @Override
        public void run() {
            synchronized (lock1) {
                System.out.println("inside my thread2 in lock2");
                System.out.println(Thread.currentThread().getState());
                synchronized (lock2) {
                    System.out.println("inside my thread2 in lock1");
                    System.out.println(Thread.currentThread().getState());
                }
            }
            System.out.println("MyThread2 is running");
        }
    }
}
