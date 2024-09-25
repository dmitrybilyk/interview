package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.volatile_keyword;

public class TaskRunner {

  private static int number;
  private static boolean ready;

  private static class Reader extends Thread {

    @Override
    public void run() {
      while (!ready) {
        Thread.yield();
      }

      System.out.println(number);
    }
  }

  public static void main(String[] args) {
    new Reader().start();
    number = 42;
    ready = true;
  }
}
