package com.conduct.interview._7_patterns.creational.factory.mess;

public class Fabric {
  public static void main(String[] args) {
    SomeParent child1 = SimpleFabric.create("1");
    child1.print();

    SomeParentFactory someParentFactory = new SomeChild1Factory();
    SomeParent child11 = someParentFactory.createSome();
    child11.print();
  }
}

class SimpleFabric {
  static SomeParent create(String someParam) {
    if (someParam.equals("1")) {
      return new SomeChild1();
    } else {
      return new SomeChild2();
    }
  }
}

class SomeParent {
  void print() {
    System.out.println("parent");
  }
}

class SomeChild1 extends SomeParent {
  void print() {
    System.out.println("child1");
  }
}

class SomeChild2 extends SomeParent {
  void print() {
    System.out.println("child2");
  }
}

abstract class SomeParentFactory {
  abstract SomeParent createSome();
}

class SomeChild1Factory extends SomeParentFactory {

  @Override
  SomeParent createSome() {
    return new SomeChild1();
  }
}

class SomeChild2Factory extends SomeParentFactory {

  @Override
  SomeParent createSome() {
    return new SomeChild2();
  }
}
