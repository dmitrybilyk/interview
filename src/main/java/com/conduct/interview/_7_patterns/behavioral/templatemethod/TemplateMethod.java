package com.conduct.interview._7_patterns.behavioral.templatemethod;

public class TemplateMethod {
  public static void main(String[] args) {
    BuildingHouse buildingHouse = new BuildingTheCheapHouse();
    buildingHouse.buildHouse();
  }
}

abstract class BuildingHouse {

  void buildHouse() {
    buildingBasement();
    buildTheFloor();
    buildTheBox();
  }

  private void buildingBasement() {
    System.out.println("Building the basement");
  }

  protected abstract void buildTheFloor();

  protected abstract void buildTheBox();
}

class BuildingTheCheapHouse extends BuildingHouse {
  public void buildTheFloor() {
    System.out.println("Building the cheap floor");
  }

  public void buildTheBox() {
    System.out.println("Building the cheap box");
  }
}

class BuildingTheLuxuryHouse extends BuildingHouse {
  public void buildTheFloor() {
    System.out.println("Building the luxury floor");
  }

  public void buildTheBox() {
    System.out.println("Building the luxury box");
  }
}
