package com.conduct.interview._1_bases.multithreading.interrupting_a_thread;

import java.util.concurrent.atomic.AtomicBoolean;

public class StopAThread implements Runnable {

  private Thread worker;
  private final AtomicBoolean running = new AtomicBoolean(false);
  private int interval;

  public StopAThread(int sleepInterval) {
    interval = sleepInterval;
  }

  public void start() {
    worker = new Thread(this);
    worker.start();
  }

  public void stop() {
    running.set(false);
  }

  public void run() {
    running.set(true);
    int i = 0;
    while (running.get()) {
      try {
        Thread.sleep(interval);
      } catch (InterruptedException e) {
        //                Thread.currentThread().interrupt();
        System.out.println("Thread was interrupted, Failed to complete operation");
      }
      i++;
      System.out.println("Some operation" + i + " in thread " + Thread.currentThread().getName());
    }
  }

  public static void main(String[] args) throws InterruptedException {
    StopAThread stopAThread = new StopAThread(1000);
    stopAThread.start();
    System.out.println("Some operation in thread " + Thread.currentThread().getName());
    Thread.sleep(5000);
    stopAThread.stop();
  }
}
