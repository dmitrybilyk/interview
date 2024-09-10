# Object-Oriented Programming (OOP) Principles

## a) Abstraction

Abstraction is the process of simplifying complex real-world objects into a programming  
model. It focuses on **details hiding**, presenting only the essential features without  
exposing the implementation details.

- **Techniques**: Interfaces and abstract classes.
- **Goal**: To show only what is necessary to the user and hide the complexities of  
  implementation.

---

## b) Encapsulation

Encapsulation is about **data hiding** and **binding** together the code and the data  
it manipulates. It ensures that a class maintains its own state and provides controlled  
access to its internal data.

- **Concepts**:
  - **Private methods and fields**: Restrict access to the internal state.
  - **Public methods**: Provide controlled access to the data.
  - **Data Integrity**: The class ensures its own state remains consistent.

---

## c) Inheritance

Inheritance allows a class to **inherit data and behaviors** from another class, promoting  
**code reuse** and **extensibility**.

- **Visibility**: Only `public` and `protected` members of the superclass are inherited.
- **Types**:
  - **Class Inheritance**: A class inherits another class.
  - **Interface Implementation**: A class implements an interface.
  - **Interface Inheritance**: An interface inherits another interface.

---

## d) Polymorphism

Polymorphism means **"many forms"** and allows a reference to point to different kinds  
of objects at different times. The actual class of the object that a reference points to  
can be determined at runtime, allowing flexibility in method behavior.

- **Types of Polymorphism in Java**:
  1. **Static Polymorphism**: Achieved through method overloading (methods with the same  
     name but different parameters).
  2. **Dynamic Polymorphism**: Achieved through method overriding (subclass provides a  
     specific implementation of a method).

---

## e) Association

Association defines how objects are **related** and **interact** with each other in OOP.

- **Composition**: A strong "belongs-to" relationship where the contained object's  
  lifecycle depends on the container. It's a **"has-a"** relationship.
- **Aggregation**: A weaker "has-a" relationship where the contained object can exist  
  independently of the container.

---

These principles form the foundation of OOP, guiding the design and structure of software  
systems to enhance modularity, reusability, and maintainability.

---

### Key Differences between OOP and FP

- **OOP**: Focuses on modeling real-world entities with classes and objects, promoting  
  code reuse, encapsulation, and inheritance.
- **FP**: Emphasizes functions and immutability, using pure functions, lambda expressions,  
  and streams for declarative and concise data processing.
