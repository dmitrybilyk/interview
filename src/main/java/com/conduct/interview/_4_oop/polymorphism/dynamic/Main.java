package com.conduct.interview._4_oop.polymorphism.dynamic;

// Parent class
class Animal {
  // Method to be overridden
  public void sound() {
    System.out.println("Animal makes a sound");
  }
}

// Child class (Dog)
class Dog extends Animal {
  @Override
  public void sound() {
    System.out.println("Dog barks");
  }
}

// Child class (Cat)
class Cat extends Animal {
  @Override
  public void sound() {
    System.out.println("Cat meows");
  }
}

public class Main {
  public static void main(String[] args) {
    // Dynamic polymorphism: the actual object is determined at runtime
    Animal myAnimal = new Dog();
    myAnimal.sound(); // Outputs: Dog barks

    myAnimal = new Cat();
    myAnimal.sound(); // Outputs: Cat meows
  }
}
