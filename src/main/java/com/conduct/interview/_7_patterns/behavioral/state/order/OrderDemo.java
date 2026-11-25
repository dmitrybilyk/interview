package com.conduct.interview._7_patterns.behavioral.state.order;

// Запусти, щоб побачити патерн у дії. Порядок читання:
//   OrderWithoutState (проблема) -> OrderState (базовий стан) ->
//   NewState / PaidState / ShippedState / DeliveredState / CancelledState -> Order (context) -> цей demo
//
// Зверни увагу: клієнт викликає ТІ САМІ методи (pay, ship, cancel),
// а результат залежить від того, в якому стані зараз замовлення.
public class OrderDemo {

    public static void main(String[] args) {
        System.out.println("=== 1. Нормальний шлях ===");
        Order order = new Order();
        order.pay();      // NEW -> PAID
        order.ship();     // PAID -> SHIPPED
        order.deliver();  // SHIPPED -> DELIVERED
        System.out.println("Final: " + order.stateName());

        System.out.println("\n=== 2. Та сама дія cancel — різна поведінка в різних станах ===");
        Order newOrder = new Order();
        newOrder.cancel();            // NEW: просто скасовує

        Order paidOrder = new Order();
        paidOrder.pay();
        paidOrder.cancel();           // PAID: скасовує І повертає гроші

        System.out.println("\n=== 3. Заборонені дії ===");
        Order notPaid = new Order();
        tryAction("ship before pay", notPaid::ship);
        // NEW не дозволяє ship -> виняток, замовлення лишається NEW

        Order shipped = new Order();
        shipped.pay();
        shipped.ship();
        tryAction("cancel after ship", shipped::cancel);
        // SHIPPED не дозволяє cancel -> занадто пізно

        tryAction("pay twice", shipped::pay);
    }

    private static void tryAction(String description, Runnable action) {
        try {
            action.run();
        } catch (IllegalStateException e) {
            System.out.println("  " + description + " -> " + e.getMessage());
        }
    }
}
