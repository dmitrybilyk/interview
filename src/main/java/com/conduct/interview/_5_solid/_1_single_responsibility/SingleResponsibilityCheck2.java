package com.conduct.interview._5_solid._1_single_responsibility;

public class SingleResponsibilityCheck2 {
    public static void main(String[] args) {

    }
}

//bad design. Because there are 3 reasons to change this class. It does too much.
// And as a result we have low coherence here and tight coupling - 3 aspects are coupled
// together
//interface CoffeeMachine {
//    void makeCoffee();
//    void payForCoffee();
//    void supplyIngredients();
//}


// Good design. Hi coherence (basically single method interfaces) and Loose coupling.
// Just one reason to change.
interface CoffeeMachine {
    void makeCoffee();
}

interface PaymentTerminal {
    void pay(String payFor);
}

interface Supplier {
    void supply();
}