package com.conduct.interview._3_spring.bean_lifecycle.phases._2Populating_properties_With_Setters;

public class PopulatingPropertiesWithSetters {
  private Dependency1 dependency1;
  private Dependency2 dependency2;

  public void setDependency1(Dependency1 dependency1) {
    this.dependency1 = dependency1;
  }

  public void setDependency2(Dependency2 dependency2) {
    this.dependency2 = dependency2;
  }
}

class Dependency1 {}

class Dependency2 {}
