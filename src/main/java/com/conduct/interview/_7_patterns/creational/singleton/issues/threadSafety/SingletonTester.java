package com.conduct.interview._7_patterns.creational.singleton.issues.threadSafety;

public class SingletonTester {
  public static void main(String[] args) {
    // Thread 1
    Thread t1 =
        new Thread(
            new Runnable() {
              @Override
              public void run() {
                SingletonTestClass instance1 = SingletonTestClass.getInstance();
                System.out.println("Instance 1 hash:" + instance1.hashCode());
              }
            });

    // Thread 2
    Thread t2 =
        new Thread(
            new Runnable() {
              @Override
              public void run() {
                SingletonTestClass instance2 = SingletonTestClass.getInstance();
                System.out.println("Instance 2 hash:" + instance2.hashCode());
              }
            });

    // start both the threads
    t1.start();
    t2.start();
  }
}

class SingletonTestClass {

  private static SingletonTestClass sSoleInstance;

  private SingletonTestClass() {} // private constructor.

  //    SOLUTION >> to make getInstance() synchronized
  public static synchronized SingletonTestClass getInstance() {
    if (sSoleInstance == null) { // if there is no instance available... create new one
      sSoleInstance = new SingletonTestClass();
    }

    return sSoleInstance;
  }
}
