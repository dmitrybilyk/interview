package com.conduct.interview._7_patterns.behavioral.state.order;

// CONCRETE STATE — кінцевий стан: замовлення доставлене.
// Жодна дія не перевизначена -> з цього стану нікуди не можна перейти.
public class DeliveredState extends OrderState {

    @Override
    public String name() {
        return "DELIVERED";
    }
}
