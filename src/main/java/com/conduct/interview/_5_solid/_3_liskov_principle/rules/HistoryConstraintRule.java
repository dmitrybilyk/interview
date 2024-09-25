package com.conduct.interview._5_solid._3_liskov_principle.rules;

public class HistoryConstraintRule {}

abstract class Car2 {

  // Allowed to be set once at the time of creation.
  // Value can only increment thereafter.
  // Value cannot be reset.
  protected int mileage;

  public Car2(int mileage) {
    this.mileage = mileage;
  }

  // Other properties and methods...

}

class ToyCar extends Car2 {
  public ToyCar(int mileage) {
    super(mileage);
  }

  public void reset() {
    mileage = 0;
  }

  // Other properties and methods
}
