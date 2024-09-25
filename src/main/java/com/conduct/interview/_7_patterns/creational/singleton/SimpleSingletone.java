package com.conduct.interview._7_patterns.creational.singleton;

public class SimpleSingletone {
  // This is init on demand. The static class is getting initialized when the method getInstance is
  // triggered
  // private static class InstanceHolder {
  //        private static final SimpleSingletone INSTANCE = new SimpleSingletone();
  //    }
  //    public static SimpleSingletone getInstance() {
  //        return InstanceHolder.INSTANCE;
  //    }

  // Early initialization with simple returning the INSTANCE from static getInstance() then
  // private static SimpleSingletone INSTANCE = new SimpleSingletone;
  private static volatile SimpleSingletone INSTANCE;

  private SimpleSingletone() {}

  public static SimpleSingletone getInstance() {

    // double-checking locking
    if (INSTANCE == null) {
      synchronized (SimpleSingletone.class) {
        if (INSTANCE == null) {
          INSTANCE = new SimpleSingletone();
        }
      }
    }
    return INSTANCE;
  }

  public static void main(String[] args) {
    SimpleSingletone s1 = SimpleSingletone.getInstance();
    SimpleSingletone s2 = SimpleSingletone.getInstance();
    // EnumSingleton s1 = EnumSingleton.INSTANCE;
    // EnumSingleton s2 = EnumSingleton.INSTANCE;

    // s1.printString();
    System.out.println(s1);
    System.out.println(s2);
  }
}

// Enum singltone is the most good one according to Effective Java (Josua Bloh)
enum EnumSingleton {
  INSTANCE;

  public void printString() {
    System.out.println("ffff");
  }
}
