package com.conduct.interview._7_patterns.behavioral.strategy.shipping;

// CONCRETE STRATEGY — один конкретний варіант алгоритму, у своєму класі.
public class StandardShipping implements ShippingStrategy {

    @Override
    public double calculate(Order order) {
        return 50 + 10 * order.weightKg();
        // базова ціна + 10 за кожен кг
    }
}
