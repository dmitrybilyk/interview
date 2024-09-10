package com.conduct.interview._5_solid.liskov_principle.rules;

public class ClassInvariantRule {
}

abstract class Car {
    protected int limit;

    // invariant: speed < limit;
    protected int speed;

    // postcondition: speed < limit
    protected abstract void accelerate();

    // Other methods...
}

class HybridCar extends Car {
    // invariant: charge >= 0;
    private int charge;

    @Override
    // postcondition: speed < limit
    protected void accelerate() {
        // Accelerate HybridCar ensuring speed < limit
    }

    // Other methods...
}