package com.conduct.interview._1_bases.multithreading.way_to_thread_safety.stateless_implementations;

import java.math.BigInteger;

public class StatelessClass {
  public static BigInteger factorial(int number) {
    BigInteger f = new BigInteger("1");
    for (int i = 2; i <= number; i++) {
      f = f.multiply(BigInteger.valueOf(i));
    }
    return f;
  }
}
