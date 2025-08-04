package com.conduct.interview._7_patterns.behavioral.templatemethod.buildhouse;

import java.util.List;

public abstract class HouseBuilder {

  void buildHouse() {
    buildingBasement();
    buildTheFloor();
    buildTheBox();
    Roof roof = createRoof();
    roof.paintRoof();

  }

  public abstract Roof createRoof();

  private void buildingBasement() {
    System.out.println("Building the basement");
  }

  protected abstract void buildTheFloor();

  protected abstract void buildTheBox();
}