package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.atomic_objects;

public class Counter {

  private int counter = 0;

  public void incrementCounter() {
    counter += 1;
  }

  public int getCounter() {
    return counter;
  }

  public static void main(String[] args) throws InterruptedException {
    Counter counter1 = new Counter();
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

  private static void extracted(Counter counter1) {
    for (int i = 0; i < 5000; i++) {
      counter1.incrementCounter();
    }
  }
}
