package com.conduct.interview._1_bases.java8.trainings;

/** Created by dik81 on 20.11.18. */
public class SomeInterfaceImpl implements SomeInterface {
  @Override
  public void someMethod(String a, Integer b) {
    someMethod(b);
    System.out.println("fdfd");
  }
}
