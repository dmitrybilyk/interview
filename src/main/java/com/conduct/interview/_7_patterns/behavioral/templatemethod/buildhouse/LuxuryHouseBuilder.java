package com.conduct.interview._7_patterns.behavioral.templatemethod.buildhouse;

public class LuxuryHouseBuilder extends HouseBuilder {
  @Override
  public Roof createRoof() {
    return new WoodRoof();
  }

  public void buildTheFloor() {
    System.out.println("Building the luxury floor");
  }

  public void buildTheBox() {
    System.out.println("Building the luxury box");
  }
}