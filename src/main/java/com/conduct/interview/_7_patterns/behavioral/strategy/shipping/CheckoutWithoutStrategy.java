package com.conduct.interview._7_patterns.behavioral.strategy.shipping;

// ПРОБЛЕМА: так виглядає код БЕЗ патерну Strategy.
// Усі способи доставки зашиті в один метод через switch.
//
// Що тут погано:
// 1) Додати новий спосіб доставки = відкрити й змінити цей клас (порушення Open/Closed principle).
// 2) Метод росте з кожним новим варіантом, логіка різних доставок перемішана в одному місці.
// 3) Не можна протестувати Express окремо від Standard — вони в одному методі.
// 4) Не можна підставити свою логіку ззовні (наприклад, акцію "безкоштовна доставка").
public class CheckoutWithoutStrategy {

    public double total(Order order, String shippingType) {
        double shipping = switch (shippingType) {
            case "STANDARD" -> 50 + 10 * order.weightKg();
            case "EXPRESS" -> 150 + 20 * order.weightKg();
            case "PICKUP" -> 0;
            // новий спосіб доставки -> ще один case ТУТ, у вже робочому коді
            default -> throw new IllegalArgumentException("Unknown shipping: " + shippingType);
        };
        return order.totalPrice() + shipping;
    }

    public static void main(String[] args) {
        Order order = new Order(1000, 2);
        CheckoutWithoutStrategy checkout = new CheckoutWithoutStrategy();
        System.out.println(checkout.total(order, "STANDARD")); // 1070.0
        System.out.println(checkout.total(order, "EXPRESS"));  // 1190.0
    }
}
