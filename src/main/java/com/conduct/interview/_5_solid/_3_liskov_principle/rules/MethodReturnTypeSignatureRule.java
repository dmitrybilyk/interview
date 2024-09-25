package com.conduct.interview._5_solid._3_liskov_principle.rules;

public class MethodReturnTypeSignatureRule {
  public static void main(String[] args) {
    Parent2 p = new Child2();
    p.someMethod(new SomeChild2());
  }
}

class Parent2 {
  protected SomeParent2 someMethod(SomeParent2 someParent) {
    return new SomeParent2();
  }
}

class Child2 extends Parent2 {
  @Override
  public SomeChild2 someMethod(SomeParent2 someParent) {
    return new SomeChild2();
    //        super.someMethod(someParent);
  }
}

class SomeParent2 {}

class SomeChild2 extends SomeParent2 {}
