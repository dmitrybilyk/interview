package com.conduct.interview._7_patterns.behavioral.strategy;

import lombok.Setter;

public class CalculationStrategyCheck {
    public static void main(String[] args) {
        Calculator calculator = new Calculator();
        calculator.setCalculateStrategy(Integer::sum);
        System.out.println(calculator.calculateResult(5, 5));
        calculator.setCalculateStrategy((a, b) -> a - b);
        System.out.println(calculator.calculateResult(5, 5));

    }
}

@FunctionalInterface
interface CalculateStrategy {
    long calculate(int a, int b);
}

@Setter
class Calculator {
    private CalculateStrategy calculateStrategy;
    public long calculateResult(int a, int b) {
        return calculateStrategy.calculate(a, b);
    }
}