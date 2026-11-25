package com.conduct.interview._7_patterns.behavioral.state.order;

// CONCRETE STATE — замовлення щойно створене.
// Дозволено: оплатити або скасувати. Відправити ще не можна (метод ship не перевизначений).
public class NewState extends OrderState {

    @Override
    public String name() {
        return "NEW";
    }

    @Override
    public void pay(Order order) {
        System.out.println("  charging customer...");
        order.changeState(new PaidState());
        // СТАН сам вирішує, куди перейти далі — це головна відмінність від Strategy
    }

    @Override
    public void cancel(Order order) {
        order.changeState(new CancelledState());
        // нічого не оплачено -> просто скасовуємо
    }
}
