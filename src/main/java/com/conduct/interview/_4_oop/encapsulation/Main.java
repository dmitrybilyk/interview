package com.conduct.interview._4_oop.encapsulation;

// Class that encapsulates data and provides controlled access
class Person {
  // Private fields (data is hidden from the outside world)
  private String name;
  private int age;

  // Constructor to initialize the Person object
  public Person(String name, int age) {
    this.name = name;
    setAge(age); // We use the setter to validate age
  }

  // Public getter for name
  public String getName() {
    return name;
  }

  // Public setter for name
  public void setName(String name) {
    this.name = name;
  }

  // Public getter for age
  public int getAge() {
    return age;
  }

  // Public setter for age (provides controlled access)
  public void setAge(int age) {
    if (age > 0 && age < 150) {
      this.age = age;
    } else {
      System.out.println("Invalid age. Please enter a valid age.");
    }
  }

  // Method that uses encapsulated data
  public void introduce() {
    System.out.println("Hi, my name is " + name + " and I am " + age + " years old.");
  }
}

public class Main {
  public static void main(String[] args) {
    // Create a Person object
    Person person = new Person("Alice", 25);

    // Access data via encapsulated methods
    person.introduce();

    // Modify data using setter (provides validation)
    person.setAge(30); // Valid age
    person.introduce();

    person.setAge(-5); // Invalid age (error message)
    person.introduce();
  }
}
