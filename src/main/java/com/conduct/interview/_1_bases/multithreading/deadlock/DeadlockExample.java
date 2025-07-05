package com.conduct.interview._1_bases.multithreading.deadlock;

public class DeadlockExample {
  static final Object SHARED_RESOURCES_A = new Object();
  static final Object SHARED_RESOURCES_B = new Object();

  public static void main(String[] args) throws InterruptedException {
    MyThread1 myThread1 = new MyThread1();
    MyThread2 myThread2 = new MyThread2();
    myThread1.start();
    myThread2.start();
    //        need this pause to show in console both threads are in blocked state
    //        Thread.sleep(3000);
    System.out.println("State of the thread 1 is " + myThread1.getState());
    System.out.println("State of the thread 2 is " + myThread2.getState());
    System.out.println("State of the thread 1 is " + myThread1.getState());
  }
}

class MyThread1 extends Thread {
  public void run() {
    synchronized (DeadlockExample.SHARED_RESOURCES_A) {
      System.out.println("Thread 1 locked shared resources A");
      synchronized (DeadlockExample.SHARED_RESOURCES_B) {
        System.out.println("Thread 1 locked shared resources B");
      }
    }
  }
}

class MyThread2 extends Thread {
  public void run() {
    synchronized (DeadlockExample.SHARED_RESOURCES_B) {
      System.out.println("Thread 2 locked shared resources B");
      synchronized (DeadlockExample.SHARED_RESOURCES_A) {
        System.out.println("Thread 2 locked shared resources A");
      }
    }
    //        to fix need to have the same locking order in both threads
    //        synchronized (DeadlockExample.SHARED_RESOURCES_A) {
    //            System.out.println("Thread 2 locked shared resources B");
    //            synchronized (DeadlockExample.SHARED_RESOURCES_B) {
    //                System.out.println("Thread 2 locked shared resources A");
    //            }
    //        }
  }
}
