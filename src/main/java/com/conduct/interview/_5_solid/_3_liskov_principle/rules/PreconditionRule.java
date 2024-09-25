package com.conduct.interview._5_solid._3_liskov_principle.rules;

public class PreconditionRule {}

class Foo {

  // precondition: 0 < num <= 5
  public void doStuff(int num) {
    if (num <= 0 || num > 5) {
      throw new IllegalArgumentException("Input out of range 1-5");
    }
    // some logic here...
  }
}
