# State Pattern

The **State Pattern** enables an object to change its behavior dynamically based  
on its internal state. Each state is encapsulated in its own class, and the  
context object delegates behavior to the current state. This pattern eliminates  
complex conditional logic and allows for cleaner transitions between states by  
letting each state manage its own transitions.

### Key Components:
- **State Interface**: Defines behavior common to all states.
- **Concrete States**: Implement specific behaviors for each state.
- **Context**: Holds the current state and switches between states dynamically.

---

# State — простими словами

**Ідея:** об'єкт поводиться по-різному залежно від того, в якому він **стані**.
Кожен стан виносимо в окремий клас. Об'єкт (Context) не вирішує сам, що робити, а передає виклик
поточному стану. **Стан сам знає**, які дії в ньому дозволені і в який стан перейти далі.

> Аналогія: кнопка на банкоматі "Видати гроші". Без картки вона нічого не робить, з карткою без PIN
> просить PIN, після PIN видає гроші. Кнопка та сама, поведінка залежить від стану банкомата.

---

## Проблема, яку він розв'язує

```java
public void ship() {
    switch (status) {
        case PAID      -> status = SHIPPED;
        case NEW       -> throw new IllegalStateException("Pay first");
        case SHIPPED   -> throw new IllegalStateException("Already shipped");
        case CANCELLED -> throw new IllegalStateException("Cancelled");
    }
}
// і такий самий switch у pay(), cancel(), deliver()...
```

- Логіка одного стану **розкидана по всіх методах**.
- Новий стан = змінити **кожен** метод.
- Легко забути `case` і отримати неможливий перехід.

Сигнал, що потрібен State: **кілька методів, і в кожному той самий `switch (status)`.**

---

## Діаграма станів (приклад `order/`)

```
              pay()               ship()               deliver()
   ┌─────┐ ─────────▶ ┌──────┐ ─────────▶ ┌─────────┐ ─────────▶ ┌───────────┐
   │ NEW │            │ PAID │            │ SHIPPED │            │ DELIVERED │
   └─────┘            └──────┘            └─────────┘            └───────────┘
      │                  │                     ✗ cancel() — занадто пізно
      │ cancel()         │ cancel() + refund
      ▼                  ▼
   ┌──────────────────────────┐
   │        CANCELLED         │
   └──────────────────────────┘
```

Що можна робити в кожному стані:

| Стан | pay | ship | deliver | cancel |
|---|---|---|---|---|
| **NEW** | ✅ → PAID | ❌ | ❌ | ✅ → CANCELLED |
| **PAID** | ❌ | ✅ → SHIPPED | ❌ | ✅ → CANCELLED (+ refund) |
| **SHIPPED** | ❌ | ❌ | ✅ → DELIVERED | ❌ |
| **DELIVERED** | ❌ | ❌ | ❌ | ❌ |
| **CANCELLED** | ❌ | ❌ | ❌ | ❌ |

Кожен **рядок** цієї таблиці — це один клас стану. Без патерну кожен **стовпчик** був би методом зі `switch`.

---

## Структура

```
   ┌─────────────────────────────┐  current  ┌──────────────────────────┐
   │       Order (Context)       │─────────▶│  «abstract» OrderState    │
   │ - state                     │           │  pay(order)     → throw   │
   │ + pay()  → state.pay(this)  │           │  ship(order)    → throw   │
   │ + ship() → state.ship(this) │           │  deliver(order) → throw   │
   │ ~ changeState(newState)     │           │  cancel(order)  → throw   │
   └──────────────▲──────────────┘           └────────────▲─────────────┘
                  │                                       │ extends
                  │          ┌────────────┬───────────────┼───────────────┬────────────────┐
                  │       NewState     PaidState     ShippedState    DeliveredState   CancelledState
                  │       pay ✅       ship ✅       deliver ✅      (нічого)         (нічого)
                  │       cancel ✅    cancel ✅
                  └──── стани викликають order.changeState(...)
```

| Роль | Що робить | У прикладі |
|---|---|---|
| **State** | спільний тип для всіх станів, одна дія = один метод | `OrderState` |
| **Concrete State** | поведінка в одному стані + перехід у наступний | `NewState`, `PaidState`, `ShippedState`… |
| **Context** | тримає поточний стан і делегує йому всі дії | `Order` |

**Як іде виклик:**
```
client:     order.ship()
Order:        state.ship(this)                ← state зараз PaidState
PaidState:      "handing over to delivery..."
PaidState:      order.changeState(new ShippedState())
Order:        state = ShippedState            ← наступний ship() поводитиметься вже інакше
```

---

## Ключові моменти

1. **Переходи робить сам стан**, а не клієнт. Клієнт лише викликає `pay()`, а що буде далі,
   вирішує `NewState`.
2. **Context передає себе (`this`) у метод стану**, щоб стан міг викликати `order.changeState(...)`.
   Так станам не треба зберігати посилання на Order у полі, і їх легко створювати.
3. **Базовий клас забороняє все за замовчуванням.** Конкретний стан перевизначає тільки дозволені дії,
   тому "що можна в цьому стані" видно одразу, а забута дія безпечно кидає виняток.
4. **`changeState` package-private:** змінювати стан можуть тільки класи станів, зовнішній код не
   може "перестрибнути" через оплату.
5. **Стани без полів можна не створювати щоразу**, а тримати як singleton-и (або взяти enum),
   бо в них немає даних, тільки поведінка.

---

## State проти Strategy

Структура в них **однакова**: Context тримає об'єкт з інтерфейсом і делегує йому роботу.
Різниця в тому, **хто і навіщо** цей об'єкт міняє.

| | Strategy (`strategy/shipping`) | State (`state/order`) |
|---|---|---|
| Що це | **як** виконати дію (спосіб) | **в якому стані** зараз об'єкт |
| Хто перемикає | **клієнт** ззовні (`setShippingStrategy`) | **самі стани** зсередини (`changeState`) |
| Знають одне про одного? | ні, стратегії незалежні | так, стан знає, який наступний |
| Скільки дій | зазвичай одна (`calculate`) | кілька (`pay`, `ship`, `cancel`…) |
| Змінюється з часом? | обирається й зазвичай не міняється | змінюється постійно, по ходу життя об'єкта |

> Коротко: **Strategy — "як зробити", обирає клієнт. State — "що я зараз можу", перемикаю себе сам.**

---

## Де трапляється

- **Життєвий цикл сутностей:** замовлення, платіж, документ (Draft → Review → Published), тікет у Jira.
- **З'єднання:** TCP (`LISTEN → ESTABLISHED → CLOSED`), WebSocket, підключення до БД.
- **UI і ігри:** плеєр (Playing / Paused / Stopped), персонаж (Idle / Running / Jumping).
- **Spring State Machine:** бібліотека, що будує такі переходи через конфігурацію.
- **Circuit Breaker (Resilience4j):** `CLOSED → OPEN → HALF_OPEN`, і кожен стан по-різному обробляє виклик.

---

## Варіант на enum

Якщо станів небагато і в них немає даних, State можна зробити через enum з методами:

```java
enum OrderStatus {
    NEW {
        OrderStatus pay()    { return PAID; }
        OrderStatus cancel() { return CANCELLED; }
    },
    PAID {
        OrderStatus ship()   { return SHIPPED; }
        OrderStatus cancel() { return CANCELLED; }
    },
    SHIPPED   { OrderStatus deliver() { return DELIVERED; } },
    DELIVERED, CANCELLED;

    OrderStatus pay()     { throw new IllegalStateException(); }
    OrderStatus ship()    { throw new IllegalStateException(); }
    OrderStatus deliver() { throw new IllegalStateException(); }
    OrderStatus cancel()  { throw new IllegalStateException(); }
}
// order.status = order.status.pay();
```

Компактно і легко зберегти в БД, але для складної логіки в станах окремі класи читаються краще.

---

## Плюси й мінуси

**Плюси:** немає `switch (status)` у кожному методі, вся логіка одного стану в одному класі,
новий стан = новий клас, неможливі переходи заборонені явно.

**Мінуси:** більше класів. Якщо станів 2–3 і переходи прості, звичайний `enum` + `if` простіший.
Переходи розкидані по класах станів, тому загальну картину краще тримати на діаграмі.

---

## Короткі відповіді для співбесіди

- **Що таке State?** Об'єкт змінює поведінку залежно від внутрішнього стану. Кожен стан в окремому
  класі, Context делегує йому дії, а стани самі перемикають Context у наступний стан.
- **Навіщо?** Прибрати однаковий `switch (status)` з усіх методів і явно описати дозволені переходи.
- **Чим відрізняється від Strategy?** Структура та сама. Strategy обирає клієнт ззовні, State
  перемикається сам зсередини, і стани знають один про одного.
- **Приклад?** Життєвий цикл замовлення, TCP-з'єднання, Circuit Breaker.

---

## Приклад у коді

Папка `order/`, читати в такому порядку:
1. `OrderWithoutState` — проблема: `switch (status)` у кожному методі.
2. `OrderState` — базовий стан: усі дії заборонені за замовчуванням.
3. `NewState`, `PaidState`, `ShippedState`, `DeliveredState`, `CancelledState` — стани.
4. `Order` — context: делегує дії поточному стану.
5. `OrderDemo` — запуск: нормальний шлях, та сама дія в різних станах, заборонені дії.
