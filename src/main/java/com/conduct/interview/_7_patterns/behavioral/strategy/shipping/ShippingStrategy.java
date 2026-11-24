package com.conduct.interview._7_patterns.behavioral.strategy.shipping;

// STRATEGY — спільний інтерфейс для всіх варіантів алгоритму.
// Context (Checkout) знає ТІЛЬКИ про цей інтерфейс, а не про конкретні класи доставки.
// Інтерфейс з одним методом -> він функціональний, тож стратегію можна передати як лямбду.
@FunctionalInterface
public interface ShippingStrategy {
    double calculate(Order order);
}
