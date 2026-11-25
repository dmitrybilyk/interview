package com.conduct.interview._7_patterns.behavioral.state.order;

// CONCRETE STATE — кінцевий стан: замовлення скасоване.
// Жодна дія не перевизначена -> з цього стану нікуди не можна перейти.
public class CancelledState extends OrderState {

    @Override
    public String name() {
        return "CANCELLED";
    }
}
