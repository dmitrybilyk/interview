package com.conduct.interview._4_oop.polymorphism.static_polymorphism;

class Calculator {
    // Method overloading: same method name, different parameters
    public int add(int a, int b) {
        return a + b;
    }

    public double add(double a, double b) {
        return a + b;
    }
}

public class Main {
    public static void main(String[] args) {
        Calculator calc = new Calculator();
        System.out.println(calc.add(5, 10));       // Calls add(int, int)
        System.out.println(calc.add(5.5, 10.5));   // Calls add(double, double)
    }
}
