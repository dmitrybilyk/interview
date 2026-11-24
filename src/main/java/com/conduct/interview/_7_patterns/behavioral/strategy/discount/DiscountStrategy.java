package com.conduct.interview._7_patterns.behavioral.strategy.discount;

// STRATEGY з методом перевірки ("self-selecting strategy").
// На відміну від звичайної стратегії, тут два методи:
//   isApplicable — "чи підходжу я для цього замовлення?" (стратегія сама вирішує)
//   discount     — сам алгоритм
// Так клієнту НЕ треба знати, яку стратегію обрати: сервіс питає кожну по черзі.
// Той самий підхід у Spring: AuthenticationProvider.supports(), HandlerAdapter.supports().
public interface DiscountStrategy {

    boolean isApplicable(Order order);

    double discount(Order order);
}
