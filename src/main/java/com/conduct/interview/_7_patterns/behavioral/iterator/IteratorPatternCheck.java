package com.conduct.interview._7_patterns.behavioral.iterator;

import java.util.ArrayList;
import java.util.List;

public class IteratorPatternCheck {
  public static void main(String[] args) {
    CarsCollection carsCollection = new CarsCollectionImpl();
    IteratorPat iteratorPat = carsCollection.iterator();
    while (iteratorPat.hasNext()) {
      System.out.println(iteratorPat.next());
    }
  }
}

class Car {
  private String name;

  public Car(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "car - " + name;
  }
}

interface IteratorPat {
  boolean hasNext();

  Object next();
}

class CarsIteratorPat implements IteratorPat {
  private int currentPosition;
  CarsCollection cars = new CarsCollectionImpl();

  public CarsIteratorPat() {
    cars.add(new Car("Opel"));
    cars.add(new Car("Honda"));
  }

  public boolean hasNext() {
    return currentPosition < cars.size();
  }

  public Car next() {
    Car car = cars.get(currentPosition);
    currentPosition++;
    return car;
  }
}

interface CarsCollection {
  IteratorPat iterator();

  void add(Car car);

  Car get(int index);

  int size();
}

class CarsCollectionImpl implements CarsCollection {
  List<Car> carsList = new ArrayList();

  public IteratorPat iterator() {
    return new CarsIteratorPat();
  }

  public void add(Car car) {
    carsList.add(car);
  }

  public Car get(int index) {
    return carsList.get(index);
  }

  public int size() {
    return carsList.size();
  }
}
