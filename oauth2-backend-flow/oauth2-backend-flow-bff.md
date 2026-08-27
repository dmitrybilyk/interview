# OAuth2 Backend Flow — BFF Pattern (крок за кроком)

> Реалізація: `oauth2-backend-flow/` — `bff-app` (порт 8188) + `resource-server-app` (порт 8189) + Keycloak (порт 8181).
> Потік: Authorization Code без PKCE, confidential client (є client_secret).

---

## Ролі

| Хто | Порт | OAuth роль |
|---|---|---|
| Браузер | — | User Agent — лише cookie `JSESSIONID`, токенів **ніколи не бачить** |
| Keycloak | 8181 | Authorization Server — логінить юзера, видає токени |
| `bff-app` | 8188 | **Client** (до Keycloak) + Resource Server (для браузера) |
| `resource-server-app` | 8189 | Resource Server (для `bff-app` як Client) |

---

## Крок 1 — `GET /login` — Бекенд будує Authorization URL

**Файл:** `FlowController.java` → метод `login()`

Що відбувається:
1. Бекенд генерує `state` — 16 випадкових байт через `SecureRandom`, перетворює в HEX-рядок.
2. Зберігає `state` в **серверній сесії** (`session.setAttribute("oauth_state", state)`).
3. Будує Authorization URL:
   ```
   GET http://localhost:8181/realms/backend-flow-realm/protocol/openid-connect/auth
     ?client_id=bff-client
     &redirect_uri=http://localhost:8188/callback
     &response_type=code
     &scope=openid profile email
     &state=<згенерований state>
   ```
4. Повертає сторінку `login.html` з цим URL — **нічого ще не відправлено до Keycloak**.
   Юзер бачить URL і натискає кнопку "Перейти до Keycloak".

**Навіщо `state`?** CSRF-захист. Коли Keycloak поверне браузер назад на `/callback`, він прийде з тим самим `state`. Бекенд порівняє: якщо не збігається — відмова.

**Навіщо `response_type=code`?** Це Authorization Code flow — Keycloak повертає не одразу токен, а `code` (одноразовий, короткоживучий код). Токен обміняємо окремо, в захищеному back-channel.

---

## Крок 2 — Браузер → Keycloak: логін юзера

Що відбувається:
1. Браузер робить `GET` до Authorization URL (повний редірект сторінки).
2. Keycloak показує форму логіну.
3. Юзер вводить `alice` / `alice123`.
4. Keycloak автентифікує юзера, створює свою сесію.
5. Keycloak **редіректить браузер назад** на `redirect_uri`:
   ```
   GET http://localhost:8188/callback?code=<короткий одноразовий код>&state=<той самий state>
   ```

Цей перехід — браузер сам, автоматично. Бекенд у цей момент нічого не робить.

**Що таке `code`?** Одноразовий авторизаційний код. Keycloak знає, якому юзеру і якому клієнту він відповідає. Живе ~1 хвилину. Не є токеном — не можна використати напряму для доступу до API.

---

## Крок 3 — `GET /callback` — Бекенд отримує code

**Файл:** `FlowController.java` → метод `callback()`

Що відбувається:
1. Браузер приходить на `/callback?code=...&state=...` — Spring MVC розбирає параметри.
2. Бекенд читає `state` з сесії (`session.getAttribute("oauth_state")`).
3. Порівнює зі `state` з URL:
   - Збігається → продовжуємо.
   - Не збігається → потенційна CSRF-атака, показуємо попередження.
4. Повертає сторінку `callback.html`, де видно `code` і статус перевірки `state`.
5. Юзер натискає "Обміняти code на токени" → `POST /exchange`.

**Що бекенд ще НЕ зробив?** Не звертався до Keycloak. `code` лише отримано і показано.

---

## Крок 4 — `POST /exchange` — Обмін code на токени (back-channel)

**Файл:** `FlowController.java` → метод `exchange()`

Це найважливіший крок — тут відбувається **back-channel запит** (сервер → сервер, без браузера).

1. Бекенд будує тіло запиту (form-encoded):
   ```
   grant_type=authorization_code
   &client_id=bff-client
   &client_secret=bff-client-secret       ← секрет, якого браузер ніколи не бачить
   &code=<code з попереднього кроку>
   &redirect_uri=http://localhost:8188/callback
   ```
2. `RestClient.post()` робить `POST http://localhost:8181/realms/.../token` — це **сервер звертається до Keycloak**, браузер не бере участі.
3. Keycloak перевіряє:
   - `client_id` + `client_secret` → чи це справді наш клієнт?
   - `code` → чи дійсний, чи не прострочений, чи відповідає `client_id`?
   - `redirect_uri` → має точно збігатися з тим, що в базі Keycloak.
4. Keycloak повертає JSON:
   ```json
   {
     "access_token":  "eyJ...",   ← для доступу до Resource Server
     "id_token":      "eyJ...",   ← дані про юзера (OIDC)
     "refresh_token": "eyJ...",   ← для оновлення access_token
     "token_type":    "Bearer",
     "expires_in":    300
   }
   ```
5. Бекенд зберігає токени в **серверній сесії** (`session.setAttribute("tokens", tokens)`).
6. Браузер бачить лише красиво відформатований JSON на сторінці — **не сам токен у cookie чи в JS**.

**Чому back-channel безпечніше?** `client_secret` ніколи не покидає сервер. Навіть якщо хтось перехопив `code` — без `client_secret` він не зможе отримати токен.

---

## Крок 5 — `GET /api/data` — Захищений endpoint BFF (лише cookie)

**Файл:** `FlowController.java` → метод `apiData()`

Це демонструє суть BFF: браузер не передає токен — він лише пред'являє cookie сесії.

1. Браузер робить `GET /api/data` — несе лише `Cookie: JSESSIONID=...`.
2. Бекенд читає `id_token` із серверної сесії.
3. Декодує payload JWT: `Base64URL.decode(id_token.split(".")[1])` — без перевірки підпису, бо токен отримано напряму від Keycloak по back-channel → вже довіряємо джерелу.
4. JWT payload містить claims:
   ```json
   {
     "sub":               "alice-uuid",
     "preferred_username":"alice",
     "email":             "alice@example.com",
     "name":              "Alice Smith",
     "iat":               1234567890,
     "exp":               1234567890
   }
   ```
5. Повертає ці дані на сторінку `api-data.html`.

**Ключовий момент:** `JSESSIONID` — звичайний cookie Spring сесії, не OAuth-токен. Бекенд сам вирішує, що з ним робити.

---

## Крок 6 — `POST /call-downstream` — BFF як Client для Resource Server

**Файл:** `FlowController.java` → метод `callDownstream()`

Тут `bff-app` перетворюється з "Resource Server для браузера" на "Client для `resource-server-app`".

1. Бекенд читає `access_token` із серверної сесії.
2. Робить server-to-server `GET http://localhost:8189/api/protected-data` з заголовком:
   ```
   Authorization: Bearer eyJ...access_token...
   ```
3. `resource-server-app` приймає запит:
   - `SecurityConfig.java`: `.oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))`
   - Spring Security читає `Authorization: Bearer ...`.
   - Йде до Keycloak JWKS endpoint (`/realms/.../protocol/openid-connect/certs`) і отримує публічний ключ.
   - Перевіряє підпис JWT локально — **не потребує звертатися до Keycloak при кожному запиті** (ключ кешується).
   - Якщо підпис валідний і токен не прострочений → повертає дані.
4. Відповідь Resource Server передається на сторінку `downstream-result.html`.

**Що `resource-server-app` НЕ робить:** не знає нічого про `client_secret`, не ходить на `/token`, не перевіряє пароль. Лише JWT.

---

## Logout

Два варіанти:

**Локальний logout (`POST /logout`):**
- `session.invalidate()` — знищує серверну сесію.
- Токени видаляються з пам'яті бекенду.
- Але! Keycloak-сесія юзера залишається активною. Якщо одразу зайти знову — Keycloak не спитає пароль, бо його сесія жива.

**Повний Keycloak logout (`GET /keycloak-logout`):**
- Браузер редіректиться на:
  ```
  GET http://localhost:8181/realms/.../protocol/openid-connect/logout
    ?client_id=bff-client
    &post_logout_redirect_uri=http://localhost:8188/
  ```
- Keycloak знищує свою сесію + інвалідує refresh_token.
- Потім редіректить браузер на `post_logout_redirect_uri`.
- Після цього потрібно буде знову вводити пароль.

---

## Що зберігається де

| Де | Що |
|---|---|
| Серверна сесія (`bff-app`) | `state`, `access_token`, `id_token`, `refresh_token` |
| Браузер (cookie) | Лише `JSESSIONID` — ідентифікатор сесії |
| JS / localStorage | Нічого — браузер токенів не бачить |
| `resource-server-app` | Нічого про юзера — лише перевіряє підпис Bearer JWT |

---

## Ключові питання для інтерв'ю

**Q: Навіщо взагалі BFF якщо є SPA?**
A: Безпека. Токени зберігаються на сервері, браузер ніколи їх не бачить → XSS не може вкрасти токен.

**Q: Що таке back-channel?**
A: Запит, який йде безпосередньо між двома серверами, минаючи браузер. В цьому потоці — POST /token від `bff-app` до Keycloak.

**Q: Чому Resource Server не потребує client_secret?**
A: Він не клієнт. Він лише перевіряє JWT підпис за допомогою публічного ключа Keycloak (JWKS). Публічний ключ — публічний, його не треба ховати.

**Q: Чи потрібен PKCE в BFF?**
A: Ні, не обов'язково — в confidential client є client_secret, який вже захищає обмін. Але PKCE додається для додаткового захисту і є good practice.

**Q: Що станеться якщо state не збігається?**
A: Потенційна CSRF-атака. Зловмисник міг підставити свій URL з чужим state щоб обманути бекенд. Якщо state не збігається — запит треба відхилити.
