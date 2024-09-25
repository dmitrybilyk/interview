package com.conduct.interview._5_solid._3_liskov_principle.rules;

public class PostconditionRule {}

abstract class Car3 {

  protected int speed;

  // postcondition: speed must reduce
  protected abstract void brake();

  // Other methods...
}

class HybridCar3 extends Car3 {

  // Some properties and other methods...

  @Override
  // postcondition: speed must reduce
  // postcondition: charge must increase
  protected void brake() {
    // Apply HybridCar brake
  }
}
