package com.conduct.interview.design_classes.zoo.model;

import java.util.ArrayList;
import java.util.List;

public class Zoo {
    private final List<Animal> animals = new ArrayList<>();

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public void feed() {
        animals.forEach(Animal::eat);
    }

    public static void main(String[] args) {
        Zoo zoo = new Zoo();
        zoo.addAnimal(new Wolf());
        zoo.addAnimal(new Gorobets());
        zoo.addAnimal(new Crocodile());

        zoo.feed();
    }

}

abstract class Animal {
    public void eat() {
        selectAppropriateFood();
        System.out.println("I'm eating");
    }

    public void makeSound() {
        System.out.println("I'm making a sound");
    }

    public abstract void move();

    public abstract void selectAppropriateFood();
}

class Mammal extends Animal {
    @Override
    public void move() {
        System.out.println("I'm moving like Mammal");
    }

    @Override
    public void selectAppropriateFood() {
        System.out.println("I'm selecting Mammal food");
    }
}

interface Flyable {
    void fly();
}

interface Crawlable {
    void crawl();
}

interface Roarable {
    void roar();
}

abstract class Bird extends Animal implements Flyable {

    @Override
    public void move() {
        System.out.println("I'm moving like Bird, flying");
        fly();
    }

    @Override
    public void fly() {
        System.out.println("The bird is flying");
    }

    @Override
    public void selectAppropriateFood() {
        System.out.println("I'm selecting Bird food");
    }
}

class Gorobets extends Bird {
    @Override
    public void fly() {
        super.fly();
        System.out.println("I'm Gorobets and I'm flying very low");
    }
}

class Sova extends Bird {
    @Override
    public void fly() {
        super.fly();
        System.out.println("I'm Sova and I'm almost not flying");
    }
}

class Reptile extends Animal implements Crawlable {

    @Override
    public void move() {
        System.out.println("I'm moving like Reptile. Crawling");
        crawl();
    }

    @Override
    public void selectAppropriateFood() {
        System.out.println("I'm selecting Reptile food");
    }

    @Override
    public void crawl() {
        System.out.println("I'm reptyl and I'm crawling");
    }
}

class Crocodile extends Reptile {
    @Override
    public void selectAppropriateFood() {
        System.out.println("I'm Crocodile and my food is really specific");
    }
}

class Wolf extends Mammal implements Roarable {

    @Override
    public void roar() {
        System.out.println("I'm Wolf and I'm roaring");
    }
}


