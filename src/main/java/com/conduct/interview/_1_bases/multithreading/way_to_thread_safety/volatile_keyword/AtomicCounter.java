package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.volatile_keyword;

import java.util.concurrent.atomic.AtomicInteger;

class Counter extends Thread {

  // Atomic counter Variable
  AtomicInteger count;

  // Constructor of class
  Counter() {
    count = new AtomicInteger();
  }

  // method which would be called upon
  // the start of execution of a thread
  public void run() {

    int max = 1_000_00_000;

    // incrementing counter total of max times
    for (int i = 0; i < max; i++) {
      count.addAndGet(1);
      // count++;
    }
  }
}

public class AtomicCounter {
  public static void main(String[] args) throws InterruptedException {
    // Instance of Counter Class
    Counter c = new Counter();

    // Defining Two different threads
    Thread first = new Thread(c, "First");
    Thread second = new Thread(c, "Second");

    // Threads start executing
    first.start();
    second.start();

    // main thread will wait for both
    // threads to complete execution
    first.join();
    second.join();

    // Printing final value of count variable
    System.out.println(c.count);
  }
}
