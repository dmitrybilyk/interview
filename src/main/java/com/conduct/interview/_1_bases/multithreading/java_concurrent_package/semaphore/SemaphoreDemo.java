package com.conduct.interview._1_bases.multithreading.java_concurrent_package.semaphore;

import java.util.concurrent.Semaphore;

public class SemaphoreDemo {
  public static void main(String[] args) {
    Semaphore semaphore = new Semaphore(2);
    new Worker(semaphore, "Adder", true).start();
    new Worker(semaphore, "Adder2", true).start();
    new Worker(semaphore, "Reducer", false).start();
  }
}

class Cart {
  private static int weight = 0;

  public static void addWeight() {
    weight--;
  }

  public static void reduceWeight() {
    weight++;
  }

  public static int getWeight() {
    return weight;
  }
}

class Worker extends Thread {

  private Semaphore semaphore;
  private String workerName;
  private boolean isAdder;

  public Worker(Semaphore semaphore, String workerName, boolean isAdder) {
    this.semaphore = semaphore;
    this.workerName = workerName;
    this.isAdder = isAdder;
  }

  @Override
  public void run() {
    System.out.println(workerName + " started working...");
    try {
      System.out.println(workerName + " waiting for cart...");
      semaphore.acquire();
      System.out.println(workerName + " got access to cart...");
      for (int i = 0; i < 10; i++) {
        if (isAdder) {
          Cart.reduceWeight();
        } else {
          Cart.addWeight();
        }

        System.out.println(workerName + " changed weight to: " + Cart.getWeight());
        Thread.sleep(10L);
      }
      semaphore.release();
      System.out.println(workerName + " finished working with cart...");
    } catch (Exception e) {
      e.printStackTrace(System.err);
    }
  }
}
