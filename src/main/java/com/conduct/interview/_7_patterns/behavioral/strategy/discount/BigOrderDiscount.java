package com.conduct.interview._7_patterns.behavioral.strategy.discount;

// CONCRETE STRATEGY — велике замовлення (від 5000) отримує 5%.
public class BigOrderDiscount implements DiscountStrategy {

    @Override
    public boolean isApplicable(Order order) {
        return order.totalPrice() >= 5000;
    }

    @Override
    public double discount(Order order) {
        return order.totalPrice() * 0.05;
    }
}
