package com.conduct.interview._7_patterns.behavioral.strategy.discount;

import java.util.List;

// Запусти, щоб побачити, як стратегія обирається САМА за даними замовлення.
// Порівняй з shipping/ShippingDemo: там стратегію обирав клієнт (користувач вибрав доставку),
// тут клієнт стратегію не обирає взагалі — просто віддає замовлення сервісу.
public class DiscountDemo {

    public static void main(String[] args) {
        DiscountService service = new DiscountService(List.of(
                new VipDiscount(),        // найвищий пріоритет
                new FirstOrderDiscount(),
                new BigOrderDiscount(),
                new NoDiscount()          // fallback — завжди останній
        ));

        System.out.println("VIP, 1000:");
        System.out.println("  " + service.finalPrice(new Order(1000, true, false)));  // VipDiscount -> 800.0

        System.out.println("First order, 1000:");
        System.out.println("  " + service.finalPrice(new Order(1000, false, true)));  // FirstOrderDiscount -> 900.0

        System.out.println("Big order, 6000:");
        System.out.println("  " + service.finalPrice(new Order(6000, false, false))); // BigOrderDiscount -> 5700.0

        System.out.println("Regular, 1000:");
        System.out.println("  " + service.finalPrice(new Order(1000, false, false))); // NoDiscount -> 1000.0

        System.out.println("VIP + first order + big, 6000:");
        System.out.println("  " + service.finalPrice(new Order(6000, true, true)));   // підходять 3, але VIP перший -> 4800.0
    }
}
