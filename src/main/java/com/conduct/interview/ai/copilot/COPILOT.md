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
