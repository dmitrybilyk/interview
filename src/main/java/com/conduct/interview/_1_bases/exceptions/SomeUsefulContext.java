package com.conduct.interview._1_bases.exceptions;

public class SomeUsefulContext {
  public void someAction(int value) throws MyCustomException {
    if (value == 0) {
      throw new MyCustomException("value is " + 0);
    }
    System.out.println(value);
  }
}
