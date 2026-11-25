package com.conduct.interview._7_patterns.behavioral.state.order;

// STATE — спільний тип для всіх станів замовлення.
// Кожна дія (pay, ship...) — окремий метод. Стан сам вирішує:
//   - дозволена ця дія в ньому чи ні;
//   - у який стан перейти після неї.
//
// Це abstract class, а не interface: за замовчуванням КОЖНА дія заборонена.
// Тоді конкретний стан перевизначає ТІЛЬКИ ті дії, які в ньому дозволені, —
// і одразу видно, що можна робити в цьому стані.
public abstract class OrderState {

    public abstract String name();

    public void pay(Order order) {
        throw notAllowed("pay");
    }

    public void ship(Order order) {
        throw notAllowed("ship");
    }

    public void deliver(Order order) {
        throw notAllowed("deliver");
    }

    public void cancel(Order order) {
        throw notAllowed("cancel");
    }

    private IllegalStateException notAllowed(String action) {
        return new IllegalStateException("Cannot " + action + " order in state " + name());
    }
}
