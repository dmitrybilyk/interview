package com.conduct.interview._1_bases.multithreading.interrupting_a_thread;

public class Baeldang {
  public static void main(String[] args) throws Exception {
    //        propagateException();
    //        System.out.println(InterruptExample.restoreTheState());
    throwCustomException();
  }

  public static void propagateException() throws InterruptedException {
    Thread.sleep(1000);
    //        Thread.currentThread().interrupt();
    if (Thread.interrupted()) {
      throw new InterruptedException();
    }
  }

  public static void throwCustomException() throws Exception {
    Thread.sleep(1000);
    Thread.currentThread().interrupt();
    if (Thread.interrupted()) {
      throw new CustomInterruptedException("This thread was interrupted");
    }
  }
}

class InterruptExample extends Thread {
  public static Boolean restoreTheState() {
    InterruptExample thread1 = new InterruptExample();
    thread1.start();
    thread1.interrupt();
    return thread1.isInterrupted();
  }

  public void run() {
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt(); // set the flag back to true
    }
  }
}

class CustomInterruptedException extends Exception {
  CustomInterruptedException(String message) {
    super(message);
  }
}
