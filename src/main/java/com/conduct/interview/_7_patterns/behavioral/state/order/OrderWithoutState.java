package com.conduct.interview._7_patterns.behavioral.state.order;

// ПРОБЛЕМА: так виглядає код БЕЗ патерну State.
// Статус зберігається в enum, і КОЖЕН метод містить switch по статусу.
//
// Що тут погано:
// 1) Логіка одного стану (наприклад, PAID) розкидана по всіх методах: pay, ship, cancel...
// 2) Новий стан (наприклад, RETURNED) = змінити КОЖЕН метод, додавши ще один case.
// 3) Легко забути case і отримати неправильний перехід (наприклад, відправити неоплачене).
// 4) З ростом кількості станів і дій switch-і стають величезними.
public class OrderWithoutState {

    enum Status { NEW, PAID, SHIPPED, DELIVERED, CANCELLED }

    private Status status = Status.NEW;

    public void pay() {
        switch (status) {
            case NEW -> status = Status.PAID;
            case PAID -> throw new IllegalStateException("Already paid");
            case SHIPPED, DELIVERED -> throw new IllegalStateException("Already paid and shipped");
            case CANCELLED -> throw new IllegalStateException("Order is cancelled");
        }
    }

    public void ship() {
        switch (status) {
            case PAID -> status = Status.SHIPPED;
            case NEW -> throw new IllegalStateException("Pay first");
            case SHIPPED, DELIVERED -> throw new IllegalStateException("Already shipped");
            case CANCELLED -> throw new IllegalStateException("Order is cancelled");
        }
    }

    public void cancel() {
        switch (status) {
            case NEW, PAID -> status = Status.CANCELLED;
            case SHIPPED, DELIVERED -> throw new IllegalStateException("Too late to cancel");
            case CANCELLED -> throw new IllegalStateException("Already cancelled");
        }
    }

    // ... і так само для deliver(). Кожна нова дія = ще один switch по ВСІХ станах.

    public static void main(String[] args) {
        OrderWithoutState order = new OrderWithoutState();
        order.pay();
        order.ship();
        System.out.println(order.status); // SHIPPED
    }
}
