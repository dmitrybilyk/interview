package com.conduct.interview._7_patterns.behavioral.state.order;

// CONCRETE STATE — замовлення оплачене.
// Дозволено: відправити або скасувати (з поверненням грошей).
public class PaidState extends OrderState {

    @Override
    public String name() {
        return "PAID";
    }

    @Override
    public void ship(Order order) {
        System.out.println("  handing over to delivery service...");
        order.changeState(new ShippedState());
    }

    @Override
    public void cancel(Order order) {
        System.out.println("  refunding money...");
        order.changeState(new CancelledState());
        // та сама дія cancel, що й у NewState, але поведінка ІНША: тут ще й повернення грошей
    }
}
