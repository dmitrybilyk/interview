package com.conduct.interview._7_patterns.structural.decorator;

public class Check {
    public static void main(String[] args) {
        CoffeeMaker coffeeMaker = new GoodCoffeeMaker(new SmellingCoffeeMaker(new CoffeeMakerImpl()));
        coffeeMaker.makeCoffee();
    }
}

interface CoffeeMaker {
    void makeCoffee();
}

class CoffeeMakerImpl implements CoffeeMaker {

    @Override
    public void makeCoffee() {
        System.out.println("making coffee");
    }
}

class CoffeeDecorator implements CoffeeMaker {
    private CoffeeMaker coffeeMaker;
    public CoffeeDecorator(CoffeeMaker coffeeMaker) {
        this.coffeeMaker = coffeeMaker;
    }

    public CoffeeDecorator() {
    }

    @Override
    public void makeCoffee() {
        coffeeMaker.makeCoffee();
//        System.out.println("making simple coffee");
    }
}

class GoodCoffeeMaker extends CoffeeDecorator {
    public GoodCoffeeMaker(CoffeeMaker coffeeMaker) {
        super(coffeeMaker);
    }

    public GoodCoffeeMaker() {
    }

    @Override
    public void makeCoffee() {
        super.makeCoffee();
        System.out.println("good");
    }
}

class SmellingCoffeeMaker extends CoffeeDecorator {
    public SmellingCoffeeMaker(CoffeeMaker coffeeMaker) {
        super(coffeeMaker);
    }

    public SmellingCoffeeMaker() {
    }

    @Override
    public void makeCoffee() {
        super.makeCoffee();
        System.out.println("good smell");
    }
}