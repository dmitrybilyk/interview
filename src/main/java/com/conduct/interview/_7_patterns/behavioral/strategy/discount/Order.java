package com.conduct.interview._7_patterns.behavioral.strategy.discount;

// Дані замовлення. За ними кожна стратегія сама вирішує, чи підходить вона.
public record Order(double totalPrice, boolean vipCustomer, boolean firstOrder) {
}
