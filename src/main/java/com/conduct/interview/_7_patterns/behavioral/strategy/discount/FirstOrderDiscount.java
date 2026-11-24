package com.conduct.interview._7_patterns.behavioral.strategy.discount;

// CONCRETE STRATEGY — перше замовлення отримує 10%.
public class FirstOrderDiscount implements DiscountStrategy {

    @Override
    public boolean isApplicable(Order order) {
        return order.firstOrder();
    }

    @Override
    public double discount(Order order) {
        return order.totalPrice() * 0.10;
    }
}
