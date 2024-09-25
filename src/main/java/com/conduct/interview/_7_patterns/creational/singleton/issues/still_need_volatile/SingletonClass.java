package com.conduct.interview._7_patterns.creational.singleton.issues.still_need_volatile;

public class SingletonClass {

  private static volatile SingletonClass sSoleInstance;

  // private constructor.
  private SingletonClass() {

    // Prevent form the reflection api.
    if (sSoleInstance != null) {
      throw new RuntimeException(
          "Use getInstance() method to get the single instance of this class.");
    }
  }

  public static SingletonClass getInstance() {
    // Double check locking pattern
    if (sSoleInstance == null) { // Check for the first time

      synchronized (SingletonClass.class) { // Check for the second time.
        // if there is no instance available... create new one
        if (sSoleInstance == null) sSoleInstance = new SingletonClass();
      }
    }

    return sSoleInstance;
  }

  public static void main(String[] args) {
    // Thread 1
    Thread t1 =
        new Thread(
            new Runnable() {
              @Override
              public void run() {
                SingletonClass instance1 = SingletonClass.getInstance();
                System.out.println("Instance 1 hash:" + instance1.hashCode());
              }
            });

    // Thread 2
    Thread t2 =
        new Thread(
            new Runnable() {
              @Override
              public void run() {
                SingletonClass instance2 = SingletonClass.getInstance();
                System.out.println("Instance 2 hash:" + instance2.hashCode());
              }
            });

    // start both the threads
    t1.start();
    t2.start();
  }
}
