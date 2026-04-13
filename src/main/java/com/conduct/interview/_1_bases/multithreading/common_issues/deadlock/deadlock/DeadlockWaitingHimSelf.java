package com.conduct.interview._1_bases.multithreading.common_issues.deadlock.deadlock;

public class DeadlockWaitingHimSelf {
  public static void main(String[] args) {
    deadlock();
  }

  public static void deadlock() {
    try {
      Thread t = new Thread(() -> {
          deadlock();
      });
      t.start();
      t.join();
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
}
