package com.conduct.interview._7_patterns.behavioral.strategy.discount;

// CONCRETE STRATEGY — "запасна" (fallback): підходить ЗАВЖДИ, тому має стояти ОСТАННЬОЮ.
// Завдяки їй сервісу не треба перевіряти "а що як жодна не підійшла" — null ніколи не буде.
public class NoDiscount implements DiscountStrategy {

    @Override
    public boolean isApplicable(Order order) {
        return true;
    }

    @Override
    public double discount(Order order) {
        return 0;
    }
}
