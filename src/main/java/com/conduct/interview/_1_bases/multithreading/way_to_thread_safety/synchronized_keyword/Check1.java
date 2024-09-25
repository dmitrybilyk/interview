package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.synchronized_keyword;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class Check1 {
  public static void main(String[] args) throws InterruptedException {
    IntHolder intHolder = new IntHolder();
    SomeService someService = new SomeService(intHolder);
    SomeService someService2 = new SomeService(intHolder);

    Thread thread = new Thread(() -> extracted(someService));
    Thread thread3 = new Thread(() -> extracted(someService));
    Thread thread2 = new Thread(() -> extracted(someService2));
    Thread thread4 = new Thread(() -> extracted(someService2));
    thread.start();
    thread3.start();
    thread2.start();
    thread4.start();
    thread.join();
    thread3.join();
    thread2.join();
    thread4.join();
    System.out.println(someService.getIntHolder().getSInt());
    System.out.println(someService2.getIntHolder().getSInt());
  }

  private static void extracted(SomeService someService) {
    for (int i = 0; i < 500; i++) {
      someService.someIncrementor();
    }
  }
}

@Getter
@Setter
@AllArgsConstructor
class SomeService {
  private IntHolder intHolder;
  private final Object mutex = new Object();

  public void someIncrementor() {
    synchronized (IntHolder.class) {
      intHolder.setSInt(intHolder.getSInt() + 1);
    }
  }
}

@Getter
@Setter
class IntHolder {
  private int sInt;
}
