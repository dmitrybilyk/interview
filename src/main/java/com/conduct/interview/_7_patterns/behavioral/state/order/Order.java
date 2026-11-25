package com.conduct.interview._7_patterns.behavioral.state.order;

// CONTEXT — об'єкт, поведінка якого залежить від стану.
// Зовні він має звичайні методи pay/ship/deliver/cancel, але сам НІЧОГО не вирішує:
// кожен метод просто передає виклик поточному стану. Жодного if/switch по статусу.
public class Order {

    private OrderState state = new NewState();
    // початковий стан

    public void pay() {
        state.pay(this);
        // передаємо себе (this), щоб стан міг перемкнути Order у наступний стан
    }

    public void ship() {
        state.ship(this);
    }

    public void deliver() {
        state.deliver(this);
    }

    public void cancel() {
        state.cancel(this);
    }

    // package-private: змінювати стан можуть тільки класи станів (вони в тому ж пакеті),
    // а не зовнішній код. Зовні можна лише викликати дії.
    void changeState(OrderState newState) {
        System.out.println("  " + state.name() + " -> " + newState.name());
        this.state = newState;
    }

    public String stateName() {
        return state.name();
    }
}
