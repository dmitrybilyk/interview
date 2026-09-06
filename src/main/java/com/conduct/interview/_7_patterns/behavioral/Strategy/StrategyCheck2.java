package com.conduct.interview._7_patterns.behavioral.strategy;

public class StrategyCheck2 {
    public static void main(String[] args) {
        SuperCalculator superCalculator = new SuperCalculator(new AddingCalculationStrategy());
        System.out.println(superCalculator.calculate(4, 3));
    }
}

class SuperCalculator {
    private CalculationStrategy calculationStrategy;
    public SuperCalculator(CalculationStrategy calculationStrategy) {
        this.calculationStrategy = calculationStrategy;
    }

    public long calculate(int a, int b) {
       return calculationStrategy.calculate(a, b);
    }
}

interface CalculationStrategy {
    long calculate(int a, int b);
}

class AddingCalculationStrategy implements CalculationStrategy {

    @Override
    public long calculate(int a, int b) {
        return a + b;
    }
}

class MultiplyingCalculationStrategy implements CalculationStrategy {

    @Override
    public long calculate(int a, int b) {
        return (long) a * b;
    }
}
