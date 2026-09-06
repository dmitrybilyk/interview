package com.conduct.interview._7_patterns.structural.bridge;

public class BridgePatternCheck {
  public static void main(String[] args) {
    Driver driver = new ExperiencedDriver();
    Car car = new LuxuryCar(driver);
    car.drive();
  }
}

abstract class Car {
  abstract void drive();
}

interface Driver {
  void driveCar();
}

class CheapCar extends Car {
  private Driver driver;

  public CheapCar(Driver driver) {
    this.driver = driver;
  }

  public void drive() {
    System.out.println("Cheap car id driving");
    driver.driveCar();
  }
}

class LuxuryCar extends Car {
  private Driver driver;

  public LuxuryCar(Driver driver) {
    this.driver = driver;
  }

  public void drive() {
    System.out.println("Luxury car id driving");
    driver.driveCar();
  }
}

class NewbeDriver implements Driver {
  public void driveCar() {
    System.out.println("Newbie driver is riding");
  }
}

class ExperiencedDriver implements Driver {
  public void driveCar() {
    System.out.println("Experienced driver is riding");
  }
}
