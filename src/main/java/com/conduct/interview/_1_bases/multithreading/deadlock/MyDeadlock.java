package com.conduct.interview._1_bases.multithreading.deadlock;

public class MyDeadlock {
  public static void main(String[] args) {
    Object lock1 = new Object();
    Object lock2 = new Object();

    new Thread(
            () -> {
              synchronized (lock1) {
                try {
                  Thread.sleep(300);
                } catch (InterruptedException e) {
                  throw new RuntimeException(e);
                }
                synchronized (lock2) {
                }
              }
            })
        .start();
    new Thread(
            () -> {
              synchronized (lock2) {
                synchronized (lock1) {
                }
              }
            })
        .start();
  }
}
