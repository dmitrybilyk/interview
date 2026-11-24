package com.conduct.interview._7_patterns.behavioral.strategy.shipping;

// CONCRETE STRATEGY — інший варіант того самого алгоритму (рахує вартість доставки),
// але з іншою логікою. Checkout про цю різницю нічого не знає.
public class ExpressShipping implements ShippingStrategy {

    @Override
    public double calculate(Order order) {
        return 150 + 20 * order.weightKg();
        // швидше, тому дорожче: вища базова ціна і вища ціна за кг
    }
}
