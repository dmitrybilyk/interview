package com.conduct.interview._5_solid._3_liskov_principle;

import java.util.Arrays;
import java.util.List;

public class LiskovPrincipleCheck2 {
    public static void main(String[] args) {
        List<CoffeeMaker> coffeeMakerList = Arrays.asList(new CappochinoCoffeeMaker(),
                new EspressoCoffeeMaker(), new NoneNaturalCoffeeMaker());

        for (CoffeeMaker coffeeMaker : coffeeMakerList) {

//            Tell, don't ask, need to fix this
//            if (coffeeMaker instanceof NoneNaturalCoffeeMaker) {
//                ((NoneNaturalCoffeeMaker) coffeeMaker).mixWithWater();
//            } else {
//                coffeeMaker.makeCoffee();
//            }
        }
    }
}

class CoffeeMaker {
    void makeCoffee() {

    }
}

class CappochinoCoffeeMaker extends CoffeeMaker {
    @Override
    void makeCoffee() {
        System.out.println("Making great cappochino in coffee machine");
    }
}

class EspressoCoffeeMaker extends CoffeeMaker {
    @Override
    void makeCoffee() {
        System.out.println("Making great espresso in coffee machine");
    }
}

class NoneNaturalCoffeeMaker extends CoffeeMaker {
    @Override
    void makeCoffee() {
//        Here we are fixing with the way - tell, don't ask
        mixWithWater();
    }

    void mixWithWater() {
        System.out.println("Mixing with water as it's not natural");
    }
}

//One more way to fix is 'breaking the hierarchi

interface NaturalCoffee {
    void makeCoffee();
}

interface NoneNaturalCoffee {
    void mixWithWater();
}

class CappochinoCoffee implements NaturalCoffee {

    @Override
    public void makeCoffee() {
        System.out.println("making capochino");
    }
}
//So here we will select the hierarchi we will work with
//class EspressonCoffee implements NaturalCoffee {
//
//    @Override
//    public void makeCoffee() {
//        System.out.println("Making espresso");
//    }
//}
//
//class NonNaturalCoffee implements NoneNaturalCoffee {
//
//    @Override
//    public void mixWithWater() {
//        System.out.println("Mixing with water");
//    }
//}


// Or if we want to use the same method we break the hierarchi - fixing hierarchi
interface Coffee {
    void makeCoffee();
}

abstract class NaturalCoffeeMaker implements Coffee {
    abstract void makeGoodCoffee();

    @Override
    public void makeCoffee() {
        makeGoodCoffee();
    }
}

abstract class NonNaturalCoffeeMaker implements Coffee {
    abstract void mixingWithWater();

    @Override
    public void makeCoffee() {
        mixingWithWater();
    }
}

class CapochinoCoffeeMaker extends NaturalCoffeeMaker {

    @Override
    void makeGoodCoffee() {
        System.out.println("making gooood capochino");
    }
}

class EspressoCoffeeMakerr extends NaturalCoffeeMaker {

    @Override
    void makeGoodCoffee() {
        System.out.println("making gooood esporesso");
    }
}

class NoneNaturalCoffeeMakerr extends NaturalCoffeeMaker {

    @Override
    void makeGoodCoffee() {
        System.out.println("mixing with water");
    }
}
