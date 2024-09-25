package com.conduct.interview._4_oop.polymorphism.polymorphic_method;

// Parent class
class Animal {
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

// A class with a method that accepts an Animal object
class AnimalTrainer {
  // Polymorphic method: accepts any Animal object and calls its sound method
  public void train(Animal animal) {
    animal.sound(); // Calls the correct sound method based on the object type
  }
}

public class Main {
  public static void main(String[] args) {
    AnimalTrainer trainer = new AnimalTrainer();

    Animal dog = new Dog();
    Animal cat = new Cat();

    trainer.train(dog); // Outputs: Dog barks
    trainer.train(cat); // Outputs: Cat meows
  }
}
