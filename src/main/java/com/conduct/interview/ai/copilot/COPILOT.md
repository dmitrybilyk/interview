# GitHub Copilot — практичні нотатки

## copilot-instructions.md

Файл `.github/copilot-instructions.md` — це **системний промпт для Copilot**.
Інжектується автоматично в кожен запит без виклику, просто лежить у репозиторії.

### Що варто вказати

**Контекст проекту** — що це за проект, щоб Copilot не гадав:
```
This is a Java microservices backend. Main stack: Spring Boot 3, Kafka, PostgreSQL, MongoDB.
```

**Стиль коду** — конвенції команди:
```
Java 21. Records over POJOs. No Lombok. No comments explaining WHAT — only WHY.
```

**Структура проекту** — де що лежить:
```
Services live in /services/<name>. Migrations in /migrations/mongo/<ticket-id>_<description>.js
```

**Середовище та інструменти** — щоб агент знав що є в терміналі:
```
## Environment
- Java: /path/to/jdk/bin/java
- keytool: /path/to/jdk/bin/keytool
- PowerShell: pwsh
- Kubernetes: kubectl, helm
- Logs: Kibana at https://kibana.internal
- Task tracker: Jira at https://jira.internal
```

**Мова відповідей**:
```
Answer questions and explain concepts in Ukrainian.
```

---

## Промпти `.github/prompts/*.prompt.md`

Промпти — це **шаблони які ти викликаєш вручну** в Copilot Chat через `#`.
На відміну від `copilot-instructions.md` — не завжди активні, тільки коли потрібні.

### mode: ask vs mode: agent

| `mode: ask` | `mode: agent` |
|---|---|
| Відповідає текстом | Виконує дії: створює файли, запускає команди |
| Ти читаєш результат | Copilot діє самостійно |
| Пояснення, Q&A, аналіз | Scaffolding, автоматизація |

---

## Корисні юз-кейси

### 1. MongoDB міграція

```markdown
---
mode: agent
---
Create a new MongoDB migration file following our conventions.

Ticket ID: [TICKET-ID]
Description: [what this migration does]

Rules:
- File path: migrations/mongo/[TICKET-ID]_[short-description].js
- Structure:
  - Header comment: ticket, date, author, description
  - `use('database_name')`
  - Idempotent operations (check before update)
  - Print result counts before and after
  - Rollback section commented at the bottom
```

### 2. Читання логів з Kibana

```markdown
---
mode: ask
---
I have logs from Kibana. Help me diagnose the issue.

What to look for:
- Root cause, not just the symptom
- First occurrence of the error (timestamp)
- Correlation ID to trace the full request
- Which service threw it and which called it
- Suggested fix

Logs:
[PASTE LOGS]
```

### 3. Jira тікет

```markdown
---
mode: ask
---
Create a Jira ticket description based on the context below.

Format:
**Summary:** (one line, verb-first: "Fix / Add / Migrate / Refactor")
**Type:** Bug | Story | Task
**Description:** what and why (2-4 sentences)
**Acceptance Criteria:** bulleted checklist
**Technical notes:** relevant files, services, or migration scripts

Context:
[DESCRIBE THE PROBLEM OR FEATURE]
```

### 4. Створити директорію для тікета в нотатках

```markdown
---
mode: agent
---
I'm starting work on a new Jira ticket. Do the following:

1. Create a directory: notes/[TICKET-ID]-[short-description]/
2. Create notes/[TICKET-ID]-[short-description]/README.md with:
   - # [TICKET-ID] — [Title]
   - ## Context (empty section)
   - ## Plan (empty section)
   - ## Notes (empty section)
3. Print the Git branch name I should use: feature/[TICKET-ID]-[short-description]

Ticket ID: [TICKET-ID]
Title: [TICKET TITLE]
```

Після цього Copilot в agent mode:
- створить директорію
- створить `README.md` з потрібною структурою
- напише тобі готову назву гілки — копіюєш і робиш `git checkout -b`

### 5. Review pull request

Два способи:

**Кнопка "Copilot review" на GitHub** — просто, без налаштувань, але generic.

**Промпт `#review-pr.prompt.md`** — знає твої конвенції з `copilot-instructions.md`, перевіряє специфічні речі:

```markdown
---
mode: agent
---
Run: `git diff main...HEAD`

Check for:
- @Transactional misuse
- N+1 queries
- OWASP top 10
- Violations of project conventions (see copilot-instructions.md)

Output per file: 🔴 Critical / 🟡 Warning / 🟢 Suggestion
```

Перевага промпту над кнопкою: враховує `copilot-instructions.md` — знає стиль коду, конвенції команди.

Додати для кожного аспекту додатку якийсь контекст з описом як працює, які класи задіяні, бібліотеки і тд

---

## `.github/instructions/*.instructions.md` — автопідхоплення per-aspect

На відміну від `.github/prompts/*.prompt.md` (викликаєш вручну через `/` чи `#prompt:`), файли в `.github/instructions/` мають frontmatter з `applyTo`, і Copilot **сам** вирішує, який файл підключити — залежно від того, з якими файлами ти зараз працюєш у чаті. Ручний виклик не потрібен.

```markdown
---
applyTo: "src/main/java/.../payments/**"
---
Payments module: обробляє транзакції через Stripe SDK.
Класи: PaymentService, PaymentGateway, WebhookHandler.
Інваріант: кожна транзакція логується в audit_log перед commit.
```

Коли відкриваєш/згадуєш файл з `payments/`, Copilot автоматично додає цей інструкційний файл у контекст. Можна мати скільки завгодно таких файлів, по одному на "аспект":

```
.github/instructions/
  payments.instructions.md          (applyTo: "**/payments/**")
  auth.instructions.md              (applyTo: "**/auth/**")
  mongo-migrations.instructions.md  (applyTo: "migrations/mongo/**")
```

### Важливі нюанси

- **Це matching по шляху (glob), не семантичний.** Copilot не "розуміє", що файл релевантний за змістом — він перевіряє, чи шлях файлу в контексті підпадає під `applyTo`. Якщо структура проекту відповідає аспектам (кожен аспект = своя папка) — працює ідеально автоматично.
- Якщо аспект **cross-cutting** (не прив'язаний до папки, напр. "як ми обробляємо idempotency" у всьому проекті) — glob не допоможе. Тоді або клади правило в `copilot-instructions.md` (завжди активний), або підключай вручну через `#file:path/to/aspect.md`.
- `copilot-instructions.md` — завжди в кожному запиті (весь текст, без розбору на секції). Якщо напхати туди описи всіх аспектів — це роздуває кожен промпт контекстом, який часто не потрібен. Розбиття на `*.instructions.md` з `applyTo` — спосіб підвантажувати контекст тільки коли він релевантний.
- **Той самий файл можна підключити і вручну** — `#file:.github/instructions/payments.instructions.md` у чаті, незалежно від того, чи співпадає `applyTo` з поточним контекстом. Тобто пишеш контекст (класи, бібліотеки, інваріанти) один раз — і далі отримуєш його або автоматично (коли glob підходить), або вручну одним `#file:` замість передруковування пояснень у кожному промпті.

### Реальні приклади для цього проекту

У цьому репо `.github/instructions/` ще не створено (зараз є тільки `copilot-instructions.md` + `prompts/*.prompt.md`). Ось як виглядали б конкретні файли під реальну структуру `src/main/java/com/conduct/interview/`:

```markdown
File: .github/instructions/leetcode.instructions.md
---
applyTo: "src/main/java/com/conduct/interview/coding/leetcode/**"
---
LeetCode розв'язки. Структура: `_<N>_<категорія>/<задача>/<Задача><Варіант>.java`
(наприклад `_1_arrays_and_hashing/two_sum/TwoSumHashMapIndicesCheck2.java`).

- Кожен файл — окремий варіант розв'язку з `main()`, що друкує результат на тестовому вході.
- Classic підходи для масивів/хешингу: HashMap one-pass, two-pointer на сортованому масиві, bucket sort для top-K.
- При рев'ю: трасуй на прикладі з `main()`, перевіряй edge cases (дублікати, порожній масив, k > розмір).
- Не виправляй стиль на "production" — навмисна мінімалістичність для повторення перед інтерв'ю.
```

```markdown
File: .github/instructions/reactive.instructions.md
---
applyTo: "src/main/java/com/conduct/interview/_9_reactive_programming/**"
---
Project Reactor демо (Mono/Flux). Теми за підпапками: cold vs hot, backpressure,
schedulers & threading, context propagation, testing reactive streams (StepVerifier).

- Пояснюючи код — завжди вказуй, на якому Scheduler виконується кожен крок.
- Якщо приклад з `block()` — зауваж, що це антипаттерн і де так робити не можна (в WebFlux-хендлерах).
- Для тестів реактивних стрімів — пропонуй `StepVerifier`, не `.block()` + assert.
```

```markdown
File: .github/instructions/kafka.instructions.md
---
applyTo: "src/main/java/com/conduct/interview/_6_message_brokers/kafka/**"
---
Kafka демо: producer/consumer, partitioning, consumer groups, offsets, idempotency.

- Середовище: Kafka піднімається через Docker (див. docker-compose в корені, якщо є) — порт 9092.
- При діагностиці проблем з доставкою — перше питання: consumer group id той самий? offset reset policy?
- Ідемпотентність на стороні consumer — через унікальний key в БД, не через Kafka exactly-once гарантії.
```

```markdown
File: .github/instructions/spring-security.instructions.md
---
applyTo: "src/main/java/com/conduct/interview/_3_spring/_13_spring_security/**"
---
Spring Security демо: автентифікація, авторизація, фільтри, JWT.

- Явно показуй порядок filter chain, якщо йдеться про `SecurityFilterChain`.
- OWASP-релевантні зауваження (CSRF, session fixation) — лінкуй з `security/owasp/` де релевантно.
```

Принцип для будь-якого нового модуля: `applyTo` = реальний шлях папки з твоєї структури, тіло — 3-5 речень про те, що там за тема, конвенція найменування файлів, і 1-2 правила специфічні саме для рев'ю/пояснення коду в цій папці (не загальні з `copilot-instructions.md`, а те, що стосується лише цього аспекту).
