package com.conduct.interview._1_bases.static_members;

public class SomeParentClass {
  static int a;

  public static int getA() {
    return a;
  }

  public static void setA(int a) {
    SomeParentClass.a = a;
  }

  static void print() {
    a = 320;
    System.out.println(a);
  }

  public static void main(String[] args) {
    SomeParentClass someParentClass = new SomeChild1();
    someParentClass.setA(123);
  }
}

class SomeChild1 extends SomeParentClass {}
