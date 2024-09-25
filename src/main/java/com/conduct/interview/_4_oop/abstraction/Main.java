package com.conduct.interview._4_oop.abstraction;

// Interface defining what actions must be implemented
interface Animal {
  void makeSound(); // Abstract method, no implementation

  void eat(); // Abstract method, no implementation
}

// Abstract class providing a base implementation for some methods
abstract class Mammal implements Animal {
  @Override
  public void eat() {
    System.out.println("This mammal is eating.");
  }

  // Abstract method that subclasses need to implement
  public abstract void move();
}

// Concrete class Dog, extending Mammal and implementing the remaining abstract methods
class Dog extends Mammal {
  @Override
  public void makeSound() {
    System.out.println("The dog barks.");
  }

  @Override
  public void move() {
    System.out.println("The dog runs on four legs.");
  }

  // This method breaks the abstraction principle
  public String getInternalDigestiveSystemDetails() {
    // Exposing unnecessary internal details about how the dog digests food
    return "The dog has a stomach, intestines, and a complex digestive system.";
  }
}

// Concrete class Cat, extending Mammal and implementing the remaining abstract methods
class Cat extends Mammal {
  @Override
  public void makeSound() {
    System.out.println("The cat meows.");
  }

  @Override
  public void move() {
    System.out.println("The cat walks gracefully.");
  }
}

// Main class to demonstrate abstraction
public class Main {
  public static void main(String[] args) {
    // We can refer to Dog and Cat as Mammal or Animal
    Animal dog = new Dog();
    Animal cat = new Cat();

    dog.makeSound();
    dog.eat();

    cat.makeSound();
    cat.eat();

    // Even though we don't know the exact implementation of these methods,
    // we can still interact with the objects based on their abstract behaviors.
  }
}
