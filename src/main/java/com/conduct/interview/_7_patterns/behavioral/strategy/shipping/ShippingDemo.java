package com.conduct.interview._7_patterns.behavioral.strategy.shipping;

import java.util.Map;

// Запусти цей клас, щоб побачити патерн у дії. Порядок читання:
//   CheckoutWithoutStrategy (проблема) -> ShippingStrategy (інтерфейс) ->
//   StandardShipping / ExpressShipping / PickupShipping (реалізації) -> Checkout (context) -> цей demo
public class ShippingDemo {

    enum ShippingType { STANDARD, EXPRESS, PICKUP }

    public static void main(String[] args) {
        Order order = new Order(1000, 2);
        // замовлення на 1000 грн, вага 2 кг

        // 1) Базове використання: стратегію передаємо в конструктор
        Checkout checkout = new Checkout(new StandardShipping());
        System.out.println("Standard: " + checkout.total(order)); // 1070.0

        // 2) Заміна стратегії під час роботи — той самий Checkout, інша поведінка
        checkout.setShippingStrategy(new ExpressShipping());
        System.out.println("Express:  " + checkout.total(order)); // 1190.0

        // 3) Стратегія як лямбда — для простої логіки окремий клас не потрібен.
        //    Наприклад, акція: доставка безкоштовна для замовлень від 500 грн.
        checkout.setShippingStrategy(o -> o.totalPrice() >= 500 ? 0 : 50);
        System.out.println("Promo:    " + checkout.total(order)); // 1000.0

        // 4) Як це роблять у реальному коді: користувач обрав доставку (прийшло з UI/API),
        //    а ми беремо готову стратегію з Map — замість switch по типу.
        //    У Spring такий Map можна отримати автоматично: Map<String, ShippingStrategy> у конструкторі.
        Map<ShippingType, ShippingStrategy> strategies = Map.of(
                ShippingType.STANDARD, new StandardShipping(),
                ShippingType.EXPRESS, new ExpressShipping(),
                ShippingType.PICKUP, new PickupShipping()
        );

        ShippingType userChoice = ShippingType.PICKUP;
        // уяви, що це значення прийшло з форми на сайті

        Checkout userCheckout = new Checkout(strategies.get(userChoice));
        System.out.println("Pickup:   " + userCheckout.total(order)); // 1000.0
    }
}
