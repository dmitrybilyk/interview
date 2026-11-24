package com.conduct.interview._7_patterns.behavioral.strategy.discount;

// CONCRETE STRATEGY — VIP-клієнт отримує 20%.
public class VipDiscount implements DiscountStrategy {

    @Override
    public boolean isApplicable(Order order) {
        return order.vipCustomer();
    }

    @Override
    public double discount(Order order) {
        return order.totalPrice() * 0.20;
    }
}
