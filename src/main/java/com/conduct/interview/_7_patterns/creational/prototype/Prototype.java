package com.conduct.interview._7_patterns.creational.prototype;

public class Prototype {
  public static void main(String[] args) {
    Car car = new Car();
    car.name = "Nissan";
    car.speed = 150;
    car.driverName = "Dmytro";
    Car newCar = (Car) car.clone();
    System.out.println(newCar);
  }
}

abstract class Vehicle {
  protected String name;
  protected int speed;

  public Vehicle(Vehicle vehicle) {
    this.name = vehicle.name;
    this.speed = vehicle.speed;
  }

  public Vehicle() {}

  public abstract Vehicle clone();
}

class Car extends Vehicle {
  public String driverName;

  public Car(Car car) {
    super(car);
    if (car != null) {
      this.driverName = car.driverName;
    }
  }

  public Car() {
    super();
  }

  @Override
  public Vehicle clone() {
    return new Car(this);
  }

  @Override
  public String toString() {
    return "Car{"
        + "name='"
        + name
        + '\''
        + "speed='"
        + speed
        + '\''
        + "driverName='"
        + driverName
        + '\''
        + '}';
  }
}

class MotoBike extends Vehicle {
  private String motoDriverName;

  public MotoBike(Vehicle vehicle) {
    super(vehicle);
  }

  @Override
  public MotoBike clone() {
    return new MotoBike(this);
  }

  @Override
  public String toString() {
    return "Motobike{"
        + "name='"
        + name
        + '\''
        + "speed='"
        + speed
        + '\''
        + "motoDriverName='"
        + motoDriverName
        + '\''
        + '}';
  }
}
