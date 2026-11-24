package com.conduct.interview._7_patterns.behavioral.strategy.discount;

import java.util.List;

// CONTEXT — отримує СПИСОК стратегій і сам знаходить потрібну, питаючи кожну isApplicable().
// Порядок у списку = пріоритет: перемагає ПЕРША стратегія, що підійшла.
// Нова знижка = новий клас + додати його в список. Сам сервіс не змінюється.
public class DiscountService {

    private final List<DiscountStrategy> strategies;

    public DiscountService(List<DiscountStrategy> strategies) {
        this.strategies = strategies;
        // у Spring цей List заповниться сам — усі біни DiscountStrategy, порядок через @Order
    }

    public double finalPrice(Order order) {
        DiscountStrategy strategy = strategies.stream()
                .filter(s -> s.isApplicable(order))
                .findFirst()
                .orElseThrow();
        // перша стратегія, яка сказала "я підходжу". NoDiscount в кінці гарантує, що знайдеться завжди

        System.out.println("  applied: " + strategy.getClass().getSimpleName());
        return order.totalPrice() - strategy.discount(order);
    }
}
