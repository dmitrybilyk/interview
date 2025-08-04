package com.conduct.interview._7_patterns.behavioral.templatemethod.buildhouse;

public class CheapHouseBuilder extends HouseBuilder {
  @Override
  public Roof createRoof() {
    return new TilesRoof();
  }

  public void buildTheFloor() {
    System.out.println("Building the cheap floor");
  }

  public void buildTheBox() {
    System.out.println("Building the cheap box");
  }
}