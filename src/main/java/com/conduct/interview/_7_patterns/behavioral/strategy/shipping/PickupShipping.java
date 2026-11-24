package com.conduct.interview._7_patterns.behavioral.strategy.shipping;

// CONCRETE STRATEGY — самовивіз. Новий варіант додано НОВИМ класом,
// жоден існуючий клас (Checkout, StandardShipping...) не змінювався — це і є Open/Closed.
public class PickupShipping implements ShippingStrategy {

    @Override
    public double calculate(Order order) {
        return 0;
    }
}
