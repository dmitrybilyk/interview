The Strategy pattern allows defining a comunity of algorithms, encapsulating
each one, and making them interchangeable. The algorithm can vary independently
of clients using it.

Components:
Strategy: Common interface for all strategies.
Concrete Strategies: Implement variations of the algorithm.
Context: Uses a strategy and can switch between them.

---

# Strategy — простими словами

**Ідея:** є одна задача ("порахувати доставку") і кілька способів її виконати (Standard, Express, Pickup).
Кожен спосіб виносимо в **окремий клас** зі спільним інтерфейсом, а клас, якому потрібен результат,
просто отримує "якийсь спосіб" і викликає його, **не знаючи, який саме**.

> Аналогія: навігатор. Маршрут до точки можна побудувати "на авто", "пішки", "громадським транспортом".
> Навігатор (Context) не змінюється — ти лише перемикаєш режим (Strategy).

---

## Проблема, яку він розв'язує

```java
double shipping = switch (type) {
    case "STANDARD" -> 50 + 10 * weight;
    case "EXPRESS"  -> 150 + 20 * weight;
    case "PICKUP"   -> 0;
    // новий спосіб -> ще один case у ВЖЕ робочому коді
};
```

- Кожен новий варіант = зміна існуючого класу (порушення **Open/Closed**).
- Усі варіанти перемішані в одному методі, їх не протестуєш окремо.
- Поведінку не можна підставити ззовні.

Сигнал, що потрібен Strategy: **`if/switch` по типу, що вибирає, ЯК виконати одну і ту саму дію.**

---

## Структура

```
          ┌────────────────────────┐   has-a    ┌─────────────────────────────┐
          │  Checkout  (Context)   │──────────▶│ «interface» ShippingStrategy │
          │                        │            │ + calculate(Order): double   │
          │ - shippingStrategy     │            └──────────────▲──────────────┘
          │ + total(order)         │                           │ implements
          └────────────────────────┘          ┌────────────────┼────────────────┐
                                              │                │                │
                                   StandardShipping   ExpressShipping   PickupShipping
                                     50 + 10·kg        150 + 20·kg          0
```

| Роль | Що робить | У прикладі |
|---|---|---|
| **Strategy** | спільний інтерфейс для всіх варіантів | `ShippingStrategy` |
| **Concrete Strategy** | один конкретний варіант алгоритму | `StandardShipping`, `ExpressShipping`, `PickupShipping` |
| **Context** | використовує стратегію, делегує їй змінну частину | `Checkout` |
| **Client** | обирає, яку стратегію дати Context | `ShippingDemo.main` |

**Як іде виклик:**
```
client:   new Checkout(new ExpressShipping())
client:   checkout.total(order)
Checkout:   shippingStrategy.calculate(order)   ← не знає, що це Express
Express:      return 150 + 20 * 2  = 190
Checkout:   return 1000 + 190 = 1190
```

---

## Ключові моменти

1. **Composition over inheritance.** Checkout не наслідує поведінку, а **містить** її в полі.
   Тому поведінку можна замінити навіть під час роботи: `setShippingStrategy(...)`.
2. **Context залежить від інтерфейсу**, а не від конкретних класів (Dependency Inversion).
3. **Новий варіант = новий клас.** Існуючий код не змінюється (Open/Closed).
4. **В Java 8+ стратегія часто є просто лямбдою**, якщо інтерфейс має один метод:
   ```java
   checkout.setShippingStrategy(order -> 0);   // акція "безкоштовна доставка"
   ```
5. **Хтось все одно має обрати стратегію.** `switch` не зникає зовсім, він переїжджає в одне місце
   (client або фабрику), або замінюється на `Map<Type, Strategy>`.

---

## Де ти вже його використовуєш

| Де | Strategy | Context |
|---|---|---|
| `list.sort(comparator)` | `Comparator` | `List.sort` / `Collections.sort` |
| `new ThreadPoolExecutor(..., handler)` | `RejectedExecutionHandler` (Abort, CallerRuns, Discard…) | `ThreadPoolExecutor` |
| `stream.filter(predicate)` | `Predicate` / `Function` | `Stream` |
| Spring Security | `PasswordEncoder` (BCrypt, Argon2…) | `AuthenticationProvider` |
| Spring | `Resource` / `ResourceLoader`, `TransactionManager` | сервіси, що їх використовують |

`Comparator` — найкращий приклад для співбесіди: `sort` один, а **як** порівнювати, ти передаєш ззовні.

---

## Strategy у Spring

Spring сам збирає всі реалізації інтерфейсу в `Map` (ключ — ім'я біна):

```java
@Component("EXPRESS")
class ExpressShipping implements ShippingStrategy { ... }

@Component("STANDARD")
class StandardShipping implements ShippingStrategy { ... }

@Service
class CheckoutService {
    private final Map<String, ShippingStrategy> strategies;

    CheckoutService(Map<String, ShippingStrategy> strategies) {   // Spring заповнить сам
        this.strategies = strategies;
    }

    double total(Order order, String type) {
        return order.totalPrice() + strategies.get(type).calculate(order);
    }
}
```

Нова доставка — новий `@Component`, `CheckoutService` не змінюється.

---

## Варіант: стратегія сама каже, чи вона підходить (`isApplicable` / `supports`)

Інколи стратегію обирає не клієнт, а **дані**. Тоді в інтерфейс додають метод перевірки:

```java
interface DiscountStrategy {
    boolean isApplicable(Order order);   // "чи підходжу я?"
    double discount(Order order);        // сам алгоритм
}
```

Context отримує **список** стратегій і бере першу, що підійшла:

```java
strategies.stream().filter(s -> s.isApplicable(order)).findFirst().orElseThrow();
```

```
Order(VIP, first, 6000)
   │
   ├─▶ VipDiscount.isApplicable?        ✅ ── береться ця, далі не дивимось
   ├─▶ FirstOrderDiscount.isApplicable?    (не перевіряється)
   ├─▶ BigOrderDiscount.isApplicable?      (не перевіряється)
   └─▶ NoDiscount.isApplicable?            (fallback — завжди ✅, стоїть останнім)
```

| | Звичайна Strategy (`shipping/`) | Self-selecting (`discount/`) |
|---|---|---|
| Хто обирає | клієнт (користувач обрав доставку) | сама стратегія за даними |
| Context отримує | одну стратегію | список стратегій |
| Важливо | — | **порядок** у списку = пріоритет, і потрібен fallback |

**Де у Spring:** `AuthenticationProvider.supports(Class)` — Spring Security перебирає провайдерів,
поки один не скаже "я вмію автентифікувати цей токен". Так само `HandlerAdapter.supports()`
у Spring MVC і `HttpMessageConverter.canRead()` (який конвертер вміє читати цей Content-Type).
У своєму коді: `List<DiscountStrategy>` у конструкторі, а порядок задається через `@Order`.

**Схоже на Chain of Responsibility**, але різниця є: у Chain кожен обробник сам викликає
наступного (`next.handle()`) і знає про ланцюжок. Тут стратегії одна про одну не знають,
перебирає їх Context.

---

## Strategy проти схожих патернів

| | Strategy | State | Template Method |
|---|---|---|---|
| Що змінюється | алгоритм | поведінка залежно від стану | кроки алгоритму |
| Хто обирає | **клієнт** ззовні | **сам об'єкт**: стани перемикають один одного | — |
| Механізм | composition (поле з інтерфейсом) | composition | **inheritance** (abstract class) |
| Стратегії знають одна про одну? | ні | так, стан знає наступний | — |

- **Strategy vs State:** структура однакова. Різниця в тому, **хто перемикає**: у Strategy — клієнт,
  у State — самі стани (`Draft → Published → Archived`).
- **Strategy vs Template Method:** Template Method фіксує скелет алгоритму в базовому класі,
  а підкласи підміняють окремі кроки. Strategy замінює **весь** алгоритм через об'єкт.

---

## Плюси й мінуси

**Плюси:** немає `if/switch` по типу, Open/Closed, кожен варіант тестується окремо, поведінка
міняється в runtime.

**Мінуси:** більше класів. Якщо варіантів 2 і вони ніколи не зміняться, звичайний `if` простіший.
Клієнт має знати, які стратегії існують, щоб обрати потрібну.

---

## Короткі відповіді для співбесіди

- **Що таке Strategy?** Сімейство взаємозамінних алгоритмів за спільним інтерфейсом. Context делегує
  роботу стратегії й не знає, яка вона.
- **Навіщо?** Прибрати `switch` по типу і додавати нові варіанти без зміни існуючого коду.
- **Приклад з JDK?** `Comparator` у `sort`, `RejectedExecutionHandler` у `ThreadPoolExecutor`.
- **Чим відрізняється від State?** Хто перемикає: клієнт (Strategy) чи сам об'єкт (State).

---

## Приклад у коді

Папка `shipping/`, читати в такому порядку:
1. `CheckoutWithoutStrategy` — проблема (switch).
2. `ShippingStrategy` — інтерфейс.
3. `StandardShipping`, `ExpressShipping`, `PickupShipping` — реалізації.
4. `Checkout` — context.
5. `ShippingDemo` — запуск: конструктор, заміна в runtime, лямбда, вибір через `Map`.

Папка `discount/` — варіант з `isApplicable`: `DiscountStrategy` → `VipDiscount`, `FirstOrderDiscount`,
`BigOrderDiscount`, `NoDiscount` → `DiscountService` (перебирає список) → `DiscountDemo`.
