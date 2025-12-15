package com.conduct.interview._5_solid._4_interface_segregation;

public class InterfaceSegregationCheck2 {
    public static void main(String[] args) {

    }
}

// This is bad design
interface CoffeeMaker {
    void makeCoffee();
    void printReceipt();
    void cookCookie();
}

class CapochinoCoffeeMaker implements CoffeeMaker {

    @Override
    public void makeCoffee() {
        System.out.println("Making Capochino");
    }

    @Override
    public void printReceipt() {
//empty
    }

    @Override
    public void cookCookie() {
// empty
    }
}

class CookieCooker implements CoffeeMaker {

    @Override
    public void makeCoffee() {

    }

    @Override
    public void printReceipt() {

    }

    @Override
    public void cookCookie() {
        System.out.println("Cooking cookie");
    }
}

//Good design

interface GoodCoffeeMaker {
    void makeCoffee();
}

interface GoodCookieCooker {
    void cookCookie();
}