package com.conduct.interview._5_solid._3_liskov_principle;

// Bad example
public class Bird {
  public void fly() {}
}

class Duck extends Bird {}

//    The duck can fly because it is a bird, but what about this:
class Ostrich extends Bird {}

//    Good example
// public class Bird{}
// public class FlyingBirds extends Bird{
//    public void fly(){}
// }
// public class Duck extends FlyingBirds{}
// public class Ostrich extends Bird{}
