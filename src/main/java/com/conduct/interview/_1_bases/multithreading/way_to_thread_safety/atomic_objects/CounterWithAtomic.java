package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.atomic_objects;

import java.util.concurrent.atomic.AtomicInteger;

public class CounterWithAtomic {

  private AtomicInteger counter = new AtomicInteger();

  public void incrementCounter() {
    counter.incrementAndGet();
  }

  public int getCounter() {
    return counter.get();
  }

  public static void main(String[] args) throws InterruptedException {
    CounterWithAtomic counter1 = new CounterWithAtomic();
    Thread thread =
        new Thread(
            () -> {
              extracted(counter1);
            });
    Thread thread2 =
        new Thread(
            () -> {
              extracted(counter1);
            });
    thread.start();
    thread2.start();
    thread.join();
    thread2.join();
    System.out.println(counter1.getCounter());
  }

  private static void extracted(CounterWithAtomic counter1) {
    for (int i = 0; i < 5000; i++) {
      counter1.incrementCounter();
    }
  }
}
