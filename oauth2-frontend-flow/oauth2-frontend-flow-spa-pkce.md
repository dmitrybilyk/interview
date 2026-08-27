# OAuth2 Frontend Flow — SPA + PKCE (крок за кроком)

> Реалізація: `oauth2-frontend-flow/` — `spa-app` (порт 8288, роздає статику) + `resource-server-app` (порт 8289) + Keycloak (порт 8281).
> Потік: Authorization Code + PKCE, **public client** (немає client_secret — браузер не може зберігати секрети).

---

## Ролі

| Хто | Порт | OAuth роль |
|---|---|---|
| Браузер (JS у `spa-app`) | 8288 | **Client** — public, PKCE, без secret |
| Keycloak | 8281 | Authorization Server |
| `resource-server-app` | 8289 | Resource Server |
| `spa-app` (Spring Boot) | 8288 | Лише роздає статичні файли — ніякої OAuth-ролі |

---

## Чому PKCE? — Ключова ідея

У BFF-флоу `bff-app` знає `client_secret` → Keycloak може перевірити, що code обмінює саме довірений клієнт.

У SPA `client_secret` неможливо сховати (код JS відкритий у браузері). PKCE вирішує це інакше:
- Браузер генерує випадковий `code_verifier` (знає лише він) і надсилає в Keycloak `code_challenge = SHA-256(code_verifier)`.
- Коли приходить обміняти `code` → надсилає `code_verifier`.
- Keycloak перевіряє: `SHA-256(code_verifier) == code_challenge`? → так, значить той самий браузер, що стартував флоу.
- Зловмисник, що перехопив лише `code`, не знає `code_verifier` → не може отримати токен.

---

## Файли SPA

| Файл | Роль |
|---|---|
| `config.js` | Keycloak URL, client_id, redirect_uri, resource server URL |
| `pkce.js` | Генерація `code_verifier`, `code_challenge`, `state` |
| `start.js` | Крок 1 — `beginLogin()` |
| `callback.js` | Крок 2+3 — `onCallbackLoaded()`, `exchangeCode()`, `callApi()` |
| `log.js` | UI-хелпер для виводу кроків на екран |
| `index.html` | Головна сторінка |
| `callback.html` | Сторінка після редіректу від Keycloak |

---

## Крок 1 — `beginLogin()` — JS генерує PKCE-пару і Authorization URL

**Файл:** `start.js`, `pkce.js`

1. **Генерація `code_verifier`** (`pkce.js: generateCodeVerifier()`):
   - `crypto.getRandomValues(new Uint8Array(32))` — 32 байти криптографічно випадкових даних.
   - Base64URL-кодування → рядок ~43 символи.
   - Залишається **лише в браузері** (в `sessionStorage`). Ніколи не надсилається до Keycloak на цьому кроці.

2. **Генерація `code_challenge`** (`pkce.js: generateCodeChallenge(verifier)`):
   - `crypto.subtle.digest('SHA-256', TextEncoder.encode(verifier))` — SHA-256 хеш verifier'а.
   - Base64URL-кодування → `code_challenge`.
   - Це **публічне** значення — надсилається до Keycloak.
   - `code_challenge_method: 'S256'` → Keycloak знає, що challenge = SHA-256(verifier).

3. **Генерація `state`** (`pkce.js: generateState()`):
   - 16 випадкових байт → Base64URL. CSRF-захист (аналогічно BFF).

4. **Збереження в sessionStorage**:
   ```js
   sessionStorage.setItem('pkce_code_verifier', verifier)
   sessionStorage.setItem('pkce_state', state)
   ```
   `sessionStorage` переживає повний редірект сторінки і повернення назад — `localStorage` теж пережив би, але `sessionStorage` ізольований на вкладку.

5. **Будування Authorization URL** (`config.js` + `start.js`):
   ```
   GET http://localhost:8281/realms/frontend-flow-realm/protocol/openid-connect/auth
     ?client_id=spa-client
     &redirect_uri=http://localhost:8288/callback.html
     &response_type=code
     &scope=openid profile email
     &state=<state>
     &code_challenge=<challenge>
     &code_challenge_method=S256
   ```
6. URL виводиться на екран. Юзер натискає посилання → браузер переходить на Keycloak.

---

## Крок 2 — Браузер → Keycloak → браузер назад

Те саме, що в BFF:
1. Браузер робить повний редірект на Authorization URL.
2. Keycloak зберігає `code_challenge` і `state` (прив'язані до майбутнього `code`).
3. Показує форму логіну — юзер вводить `alice` / `alice123`.
4. Keycloak видає `code`, редіректить браузер:
   ```
   GET http://localhost:8288/callback.html?code=<code>&state=<state>
   ```

**Де тут різниця з BFF?** У BFF Keycloak редіректив на `http://localhost:8188/callback` — Spring контролер. Тут — на `callback.html` — статична HTML-сторінка, яку обробляє JS у браузері.

---

## Крок 3 — `onCallbackLoaded()` — JS читає code і перевіряє state

**Файл:** `callback.js`

Запускається автоматично при завантаженні `callback.html`.

1. Читає URL-параметри:
   ```js
   const params = new URLSearchParams(window.location.search)
   const code = params.get('code')
   const state = params.get('state')
   ```
2. Читає збережений state:
   ```js
   const expectedState = sessionStorage.getItem('pkce_state')
   const stateOk = state && state === expectedState
   ```
3. Виводить `code` на екран. Показує статус перевірки state.
4. Зберігає `code` в `window.__code` для наступного кроку.

**Нічого ще не надіслано до Keycloak.**

---

## Крок 4 — `exchangeCode()` — Браузер сам обмінює code на токени

**Файл:** `callback.js`

Ключова різниця від BFF: **JS у браузері** робить `fetch()` напряму до Keycloak. Немає проміжного сервера.

1. Читає `code_verifier` із sessionStorage.
2. Будує тіло запиту:
   ```
   grant_type=authorization_code
   &client_id=spa-client
   &code=<code>
   &redirect_uri=http://localhost:8288/callback.html
   &code_verifier=<verifier>          ← замість client_secret!
   ```
3. `fetch(CONFIG.tokenEndpoint, { method: 'POST', body: body })` — браузер напряму звертається до:
   ```
   POST http://localhost:8281/realms/frontend-flow-realm/protocol/openid-connect/token
   ```
4. Keycloak перевіряє:
   - Знаходить `code_challenge` збережений при /auth запиті.
   - Обчислює `SHA-256(code_verifier)`.
   - Порівнює з `code_challenge` → якщо збігається — той самий браузер, дозволяємо.
   - Немає `client_secret` — це ок, `spa-client` зареєстрований як public client.
5. Повертає токени:
   ```json
   {
     "access_token":  "eyJ...",
     "id_token":      "eyJ...",
     "refresh_token": "eyJ...",
     "token_type":    "Bearer",
     "expires_in":    300
   }
   ```
6. JS зберігає в `lastTokens` (JS-пам'ять, не localStorage, не cookie).

**Що бачить браузер?** Повну відповідь з токенами — вони в JS-пам'яті. XSS може їх вкрасти. Тому:
- Зберігати в localStorage — погано (XSS доступ).
- В httpOnly cookie — краще, але не стандарт для SPA.
- В JS memory (`lastTokens`) — прийнятно, токен живе лише до перезавантаження сторінки.

---

## Крок 5 — `callApi()` — Браузер напряму викликає Resource Server

**Файл:** `callback.js`

```js
fetch(`http://localhost:8289/api/protected-data`, {
    headers: { Authorization: `Bearer ${lastTokens.access_token}` }
})
```

**Браузер звертається до Resource Server напряму (cross-origin!).**

Resource Server (`SecurityConfig.java` frontend-flow):
1. Отримує `Authorization: Bearer eyJ...`.
2. Декодує JWT, перевіряє підпис через JWKS від Keycloak.
3. Якщо валідний → повертає дані.
4. **CORS**: на відміну від BFF, тут Resource Server має бути налаштований на CORS, бо JS у браузері (origin `http://localhost:8288`) звертається до іншого origin (`http://localhost:8289`).
   ```java
   config.setAllowedOrigins(List.of("http://localhost:8288"))
   config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"))
   config.setAllowedHeaders(List.of("Authorization", "Content-Type"))
   ```
5. Без CORS-заголовків браузер заблокував би відповідь — `SecurityConfig` у BFF-флоу CORS не налаштовує, бо там `bff-app` → `resource-server-app` йде сервер-сервер.

---

## Порівняння BFF vs SPA PKCE

| Характеристика | BFF | SPA + PKCE |
|---|---|---|
| Де виконується OAuth-логіка | Сервер (`bff-app`) | Браузер (JS) |
| Токени в браузері | Ніколи | Так (JS memory) |
| `client_secret` | Так (confidential client) | Ні (public client) |
| PKCE | Опціонально | Обов'язково (замість secret) |
| CORS на Resource Server | Не потрібен | Обов'язковий |
| XSS-ризик | Низький (токени на сервері) | Вищий (токени в JS) |
| Cookie | `JSESSIONID` (server session) | Не використовується для auth |
| Підходить для | Традиційні веб-додатки, мікросервіси | SPA (React, Angular, Vue) |

---

## Що де зберігається (SPA)

| Де | Що |
|---|---|
| `sessionStorage` | `pkce_code_verifier`, `pkce_state` — тільки на час флоу |
| JS-пам'ять (`lastTokens`) | `access_token`, `id_token`, `refresh_token` |
| Мережа (DevTools → Network) | Всі запити видно: до Keycloak /auth, /token, до Resource Server |
| Сервер (`spa-app`) | Нічого — він лише роздає статику |

---

## Ключові питання для інтерв'ю

**Q: Чому SPA не може використовувати client_secret?**
A: JS-код відкритий у браузері. Будь-хто може відкрити DevTools або view-source і прочитати secret. "Public client" — це клієнт, якому не можна довірити secret.

**Q: Що гарантує PKCE?**
A: Що `code` обмінює той самий браузер (та сама вкладка), що його отримав. Зловмисник, що перехопив code (наприклад через referer header), не знає `code_verifier` і не може отримати токен.

**Q: Чому `sessionStorage`, а не `localStorage`?**
A: `sessionStorage` ізольований на вкладку. Якщо відкрити нову вкладку — там не буде `code_verifier`. `localStorage` — глобальний для домену, доступний усім вкладкам і JS на сторінці → трохи більший ризик.

**Q: Навіщо CORS на Resource Server в SPA, але не в BFF?**
A: У BFF `bff-app` звертається до `resource-server-app` сервер-сервер (HTTP від Java-коду). У SPA браузер звертається напряму через `fetch()` → origin відрізняється → браузер сам перевіряє CORS-заголовки.

**Q: Де безпечніше зберігати токени в SPA?**
A: Ідеального рішення нема. JS memory (`let token = ...`) — токен не переживає перезавантаження. httpOnly cookie — захищений від XSS, але потребує бекенд для встановлення. localStorage/sessionStorage — доступний JS, небезпечний при XSS. Кращий компроміс — httpOnly cookie через тонкий token proxy або перехід на BFF.

**Q: Чи потрібен `id_token` для звернення до API?**
A: Ні. `id_token` — це про аутентифікацію (хто ти). `access_token` — про авторизацію (що тобі дозволено). Resource Server перевіряє лише `access_token`.
