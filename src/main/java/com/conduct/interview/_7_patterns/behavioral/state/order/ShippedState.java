package com.conduct.interview._7_patterns.behavioral.state.order;

// CONCRETE STATE — замовлення в дорозі.
// Дозволено: тільки доставити. Скасувати вже пізно (cancel не перевизначений -> виняток).
public class ShippedState extends OrderState {

    @Override
    public String name() {
        return "SHIPPED";
    }

    @Override
    public void deliver(Order order) {
        order.changeState(new DeliveredState());
    }
}
