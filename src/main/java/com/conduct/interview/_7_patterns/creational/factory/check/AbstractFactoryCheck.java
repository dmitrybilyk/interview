package com.conduct.interview._7_patterns.creational.factory.check;

public class AbstractFactoryCheck {
    AnimalAbstractFactory factory = new GoodAnimalAbstractFactory();
    Horse horse = factory.createHorse();
    Cat cat = factory.createCat();
}

class Animal{
}

class Horse extends Animal {}
class GoodHorse extends Horse {}
class BadHorse extends Horse {}
class Cat extends Animal {}
class GoodCat extends Cat {}
class BadCat extends Cat {}

abstract class AnimalAbstractFactory {
    abstract Horse createHorse();
    abstract Cat createCat();
}

class GoodAnimalAbstractFactory extends AnimalAbstractFactory {

    @Override
    Horse createHorse() {
        return new GoodHorse();
    }

    @Override
    Cat createCat() {
        return new GoodCat();
    }
}

class BadAnimalAbstractFactory extends AnimalAbstractFactory {

    @Override
    Horse createHorse() {
        return new BadHorse();
    }

    @Override
    Cat createCat() {
        return new BadCat();
    }
}