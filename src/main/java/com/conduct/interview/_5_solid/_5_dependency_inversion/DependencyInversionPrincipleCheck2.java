package com.conduct.interview._5_solid._5_dependency_inversion;

public class DependencyInversionPrincipleCheck2 {
    public static void main(String[] args) {

    }
}

class CoffeeMaker {
    void makeCoffee() {
//        Bad design. Should not depend on implementations
        CoffeeMachine coffeeMachine = new CoffeeMachine();
        coffeeMachine.prepareCoffee();
    }
}

class CoffeeMachine {
    void prepareCoffee() {
        System.out.println("Preparing coffee");
    }
}

//Now here is good design. We depend on abstractions. Implementations should also depend on
// abstractions. Here we use also Dependency injection. It can be done with constructor, setter,
// in Spring also using proxy (Autowired annotation)
//BTW invercion of control is the process when dependencies are injected in different thread
//for us, usually by some Framework like Spring
interface GoodCoffeeMaker {
    void makeCoffee();
}

class GoodCoffeeMakerImpl implements GoodCoffeeMaker{
    private final GoodCoffeeMachine coffeeMachine;

    GoodCoffeeMakerImpl(GoodCoffeeMachine coffeeMachine) {
        this.coffeeMachine = coffeeMachine;
    }

    @Override
    public void makeCoffee() {
        coffeeMachine.prepareCoffee();
    }
}

interface GoodCoffeeMachine {
    public void prepareCoffee();
}

class CoffeeMachineImpl implements GoodCoffeeMachine {
    public void prepareCoffee() {
        System.out.println("Preparing coffee");
    }
}
