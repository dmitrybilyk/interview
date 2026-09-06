# Auth Theory: Authentication, Authorization, OAuth2, OIDC, SSO

---

## Authentication vs Authorization

### Authentication (AuthN) — "Хто ти?"
Перевірка особи. Ти доводиш, що ти — це ти.

- Надаєш логін/пароль, біометрію, OTP
- Система каже: "Так, це Аліса"
- Результат: **identity** (ідентичність)

### Authorization (AuthZ) — "Що тобі можна?"
Перевірка прав. Система вирішує, що Аліса може робити.

- Після аутентифікації: "Аліса може читати /orders, але не /admin"
- Результат: **permissions / scopes / roles**

> Спочатку — Authentication. Потім — Authorization.
> Не можна авторизувати того, кого ще не ідентифікував.

---

## OAuth2

**OAuth2** — це стандарт **авторизації** (RFC 6749).

Він вирішує одну задачу: як дати додатку доступ до твоїх ресурсів,
**не передаючи йому твій пароль**.

### Учасники

| Роль | Хто | Що робить |
|:--|:--|:--|
| **Resource Owner** | Користувач | Власник даних, дає дозвіл |
| **Client** | Твій додаток | Хоче доступ до даних |
| **Authorization Server** | Keycloak / Google Auth | Перевіряє юзера, видає токени |
| **Resource Server** | API | Захищений ресурс, перевіряє токен |

### Що OAuth2 дає

Замість пароля — **access_token**. Це обмежений за часом і scope ключ доступу.

```
Client отримує:  access_token  (для API)
                 refresh_token (щоб оновити access_token)
```

### Що OAuth2 НЕ дає

OAuth2 **не говорить, хто цей юзер**. Він тільки видає доступ.
Для ідентифікації потрібен OIDC.

### Grant types (способи отримати токен)

| Grant Type | Коли використовується |
|:--|:--|
| **Authorization Code** | Web-app / мобільний: юзер логіниться через браузер |
| **Authorization Code + PKCE** | SPA / мобільний без backend: без `client_secret` |
| **Client Credentials** | Server-to-server: без юзера |
| **Refresh Token** | Оновлення access_token без повторного логіну |

---

## OIDC (OpenID Connect)

**OIDC** — це шар поверх OAuth2, який додає **аутентифікацію**.

OAuth2 відповідає на "що можна?", OIDC додає "хто це?".

### Що додає OIDC

- **id_token** — JWT із даними про юзера (`sub`, `email`, `name`)
- **UserInfo endpoint** — `/userinfo` для додаткових claims
- **scope `openid`** — обов'язковий, вмикає OIDC поверх OAuth2

```
Запит:  scope=openid profile email
Отримуємо:  access_token  (OAuth2 — для API)
            id_token      (OIDC  — хто залогінився)
            refresh_token
```

### id_token (JWT)

```json
{
  "sub": "dc55f38a-ca4c-45c5-ace4-920fb49893ed",
  "email": "alice@example.com",
  "name": "Alice Demo",
  "iss": "http://localhost:8191/realms/bff-real-realm",
  "aud": "bff-real-client",
  "exp": 1753682523,
  "iat": 1753682223
}
```

- `sub` — унікальний ID юзера (не змінюється)
- `iss` — хто видав токен (Authorization Server)
- `aud` — для кого токен (твій `client_id`)
- `exp` — коли закінчується

### OAuth2 vs OIDC — коротко

| | OAuth2 | OIDC |
|:--|:--|:--|
| Призначення | Authorization | Authentication |
| Питання | "Що можна?" | "Хто це?" |
| Токен | access_token | id_token (JWT) |
| Активується | завжди | `scope=openid` |

---

## SSO (Single Sign-On)

**SSO** — коли одне логінування дає доступ до багатьох додатків.

### Як працює

1. Юзер логіниться в Keycloak → отримує **session** у Keycloak
2. Відкриває App2 → App2 редіректить на Keycloak
3. Keycloak бачить активну сесію → **не питає пароль знову**
4. Видає токени для App2 автоматично

### Де зберігається сесія

У Keycloak є **SSO session** (зберігається на сервері + cookie `KEYCLOAK_SESSION` у браузері).
Ця session відокремлена від сесій твоїх додатків.

### Logout у SSO

Є два варіанти:

**Local logout** — тільки твій додаток забуває сесію. Інші додатки і Keycloak — не знають.

**Global logout (Back-channel / Front-channel)** — Keycloak інвалідує SSO session і сповіщає всі підключені додатки.

---

## JWT — як перевіряється токен

Resource Server перевіряє підпис JWT **без звернення до Authorization Server** (stateless).

```
1. Отримати JWKS: GET /realms/{realm}/protocol/openid-connect/certs
2. Взяти `kid` з JWT header
3. Знайти відповідний публічний ключ у JWKS
4. Перевірити підпис RSA/ECDSA
5. Перевірити exp (не протермінований)
6. Перевірити iss (відповідає очікуваному realm)
7. Перевірити aud (твій client_id або resource server)
```

Ключі кешуються — реальне звернення до Keycloak відбувається рідко (тільки при ротації ключів).

---

## Flows — коротке порівняння

### Backend-controlled (Authorization Code, BFF pattern)

```
Browser → BFF → Keycloak (back-channel для токенів)
Токени зберігаються на сервері. Браузер отримує лише httpOnly cookie.
```
- Безпечно: токени ніколи не в браузері
- Потребує backend

### SPA + PKCE (Authorization Code + PKCE)

```
Browser → Keycloak (code) → Browser POST /token з code_verifier
Токени зберігаються в браузері (memory / sessionStorage).
```
- Немає `client_secret` (публічний клієнт)
- PKCE замінює secret: `code_verifier` → `code_challenge = SHA-256(verifier)`
- Токени доступні JS → ризик XSS

---

## Refresh token

Коли `access_token` протермінується (зазвичай 5 хв):

```
POST /token
grant_type=refresh_token
refresh_token=...
client_id=...
client_secret=...   ← тільки для confidential clients
```

Отримуємо новий `access_token` (і новий `refresh_token`).
`client_secret` потрібен тільки для обміну токенів, **не** для перевірки JWT.

---

## Keycloak — що це

Keycloak — open-source Authorization Server і Identity Provider (IdP).

Реалізує OAuth2 + OIDC + SSO + SAML.

Ключові поняття:
- **Realm** — ізольована область (окремий tenant). Своя БД юзерів, свої клієнти.
- **Client** — зареєстрований додаток (`client_id` + `client_secret`)
- **Scope** — на що клієнт просить дозвіл (`openid`, `profile`, `email`, кастомні)
- **Role** — роль юзера (йде в токен як claim)
