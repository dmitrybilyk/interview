package com.conduct.interview._5_solid._2_open_closed_principle;

public class OpenClosedPrincipleCheck2 {
    public static void main(String[] args) {
        CoffeeProcessor coffeeProcessor = new CappochinoCoffeeProcessor();
        CoffeeMaker coffeeMaker = new CoffeeMaker();
        coffeeMaker.makeCoffee(coffeeProcessor);
    }
}

// Bad design. Bcause if we add one more coffee type then we make changes in main business class
//class CoffeeMaker {
//    void makeCoffee(String coffeeType) {
//        if ("Cappochino".equals(coffeeType)) {
//            System.out.println("Preparing Cappochino");
//        } else if ("Espresso".equals(coffeeType)) {
//            System.out.println("Preparing Espresso");
//        }
//    }
//}


// Good design. We don't make changes in main business code in case we
// want to add more types of coffee
class CoffeeMaker {
    void makeCoffee(CoffeeProcessor coffeeProcessor) {
        System.out.println("Preparing coffee");
        coffeeProcessor.processCoffee();
    }
}

interface CoffeeProcessor {
    void processCoffee();
}

class CappochinoCoffeeProcessor implements CoffeeProcessor {

    @Override
    public void processCoffee() {
        System.out.println("Preparing coffee");
    }
}

class EspressoCoffeeProcessor implements CoffeeProcessor {

    @Override
    public void processCoffee() {
        System.out.println("Preparing Espresso");
    }
}