package com.conduct.interview._4_oop.inheritance;

// Base class (also called the parent class or super class)
class Animal {
    String name;

    // Constructor for Animal
    Animal(String name) {
        this.name = name;
    }

    // Method that all animals can do
    void makeSound() {
        System.out.println("Some generic animal sound");
    }

    // Method to print the animal's name
    void printName() {
        System.out.println("Animal name: " + name);
    }
}

// Derived class (also called the child class or subclass)
class Dog extends Animal {
    
    // Constructor for Dog, it calls the constructor of the parent class
    Dog(String name) {
        super(name); // 'super' calls the constructor of the Animal class
    }

    // Overriding the makeSound method of the Animal class
    @Override
    void makeSound() {
        System.out.println("Woof Woof");
    }

    // New method specific to Dog class
    void fetch() {
        System.out.println(name + " is fetching a ball.");
    }
}

public class Main {
    public static void main(String[] args) {
        // Creating an object of the Dog class
        Dog myDog = new Dog("Buddy");

        // Accessing methods from the Dog class and the Animal class
        myDog.printName();    // Inherited method from Animal
        myDog.makeSound();    // Overridden method from Animal
        myDog.fetch();        // Method specific to Dog class
    }
}
