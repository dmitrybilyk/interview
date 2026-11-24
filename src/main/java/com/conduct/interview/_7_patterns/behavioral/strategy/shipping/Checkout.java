package com.conduct.interview._7_patterns.behavioral.strategy.shipping;

// CONTEXT — клас, який КОРИСТУЄТЬСЯ стратегією.
// Він має свою логіку (порахувати загальну суму), а змінну частину (вартість доставки)
// делегує стратегії. Яка саме стратегія — йому байдуже, він знає лише інтерфейс.
public class Checkout {

    private ShippingStrategy shippingStrategy;

    public Checkout(ShippingStrategy shippingStrategy) {
        this.shippingStrategy = shippingStrategy;
        // стратегію передають ЗЗОВНІ (composition) — Checkout сам її не створює
    }

    public void setShippingStrategy(ShippingStrategy shippingStrategy) {
        this.shippingStrategy = shippingStrategy;
        // стратегію можна замінити під час роботи програми (runtime)
    }

    public double total(Order order) {
        double shipping = shippingStrategy.calculate(order);
        // делегуємо: "порахуй доставку, як ти вмієш" — без жодних if/switch
        return order.totalPrice() + shipping;
    }
}
