package com.conduct.interview._7_patterns.behavioral.strategy.shipping;

// Дані, з якими працюють усі стратегії. Стратегії отримують Order і самі вирішують,
// які поля їм потрібні (вага, сума тощо).
public record Order(double totalPrice, double weightKg) {
}
