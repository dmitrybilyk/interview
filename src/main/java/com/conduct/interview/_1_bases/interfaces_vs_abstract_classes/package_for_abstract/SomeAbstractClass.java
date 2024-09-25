package com.conduct.interview._1_bases.interfaces_vs_abstract_classes.package_for_abstract;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SomeAbstractClass {
  int intValue;

  public void print(String someThingToPrint) {
    System.out.println(intValue);
  }

  //    protected abstract void printAbstract() ;
  protected void printNonAbstract() {
    System.out.println("non abstract");
  }
  ;
}
