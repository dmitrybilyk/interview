package com.conduct.interview._7_patterns.behavioral.memento;

// Memento class
class Memento {
    private String name;
    private int age;
    private String email;

    // Constructor to store all fields
    public Memento(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    // Getters to retrieve saved states
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }
}

// Originator class
class Originator {
    private String name;
    private int age;
    private String email;

    // Set state
    public void setState(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
        System.out.println("State set to: Name = " + name + ", Age = " + age + ", Email.kt = " + email);
    }

    // Create a Memento with all fields
    public Memento saveStateToMemento() {
        System.out.println("Saving state to memento.");
        return new Memento(name, age, email);
    }

    // Restore state from Memento
    public void restoreStateFromMemento(Memento memento) {
        this.name = memento.getName();
        this.age = memento.getAge();
        this.email = memento.getEmail();
        System.out.println("State restored from memento: Name = " + name + ", Age = " + age + ", Email.kt = " + email);
    }
}

// Caretaker class
class Caretaker {
    private Memento memento;

    // Save Memento
    public void saveMemento(Memento memento) {
        this.memento = memento;
    }

    // Retrieve Memento
    public Memento retrieveMemento() {
        return memento;
    }
}

// Client code
public class MementoPatternDemo {
    public static void main(String[] args) {
        Originator originator = new Originator();
        Caretaker caretaker = new Caretaker();

        // Set state and save it
        originator.setState("John Doe", 30, "john@example.com");
        caretaker.saveMemento(originator.saveStateToMemento());

        // Change state and then restore from the memento
        originator.setState("Jane Smith", 25, "jane@example.com");
        originator.restoreStateFromMemento(caretaker.retrieveMemento());
    }
}
