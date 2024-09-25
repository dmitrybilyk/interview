package com.conduct.interview._1_bases.java8.practise;

/** Created by dik81 on 15.11.18. */
public class Main {

  public static void main(String[] args) {
    execute(() -> System.out.println("fdfd"));
  }

  public static void execute(OurFunctionalInterface ourFunctionalInterface) {
    ourFunctionalInterface.test();
  }
}
