# 🔐 OAuth 2.0 & OIDC: Core Concepts

### 📌 Що таке OAuth 2.0?
**OAuth 2.0** — це протокол **авторизації**, який дозволяє додатку отримати обмежений доступ до ресурсів користувача на іншому сервісі (Google, Keycloak) без передачі пароля.
*   **Ключова ідея:** Делегування прав (Delegation of Permissions).
*   **OIDC (OpenID Connect):** Надбудова над OAuth 2.0, яка додає шар **автентифікації** (ID Token), щоб знати, *хто* цей користувач.

---

### 👥 Ролі в OAuth 2.0

| Роль | Опис | Приклад |
| :--- | :--- | :--- |
| **Resource Owner** | Власник даних (користувач). | Ви (Дмитро). |
| **Client** | Додаток, що запитує доступ. | React App, Zoom, Postman. |
| **Authorization Server** | Сервер, що перевіряє юзера і видає токени. | Keycloak, Google Auth Server. |
| **Resource Server** | Сервер, де лежать захищені дані. | Ваш Java/Kotlin Backend. |

---

### 🎫 Типи токенів (JWT)

*   **Access Token:** Квиток для доступу до API. Містить **Scopes** (дозволи).
*   **Refresh Token:** Використовується для оновлення Access Token без повторного логіну.
*   **ID Token (OIDC):** Паспорт користувача. Містить дані про особу (name, email, sub).

---

### 🚀 Основні тези для інтерв'ю

*   **OAuth2 vs OIDC:** OAuth2 — "що мені дозволено" (Authorization). OIDC — "хто я такий" (Authentication).
*   **Scopes (Області доступу):** Межі делегованих прав (наприклад, `read`, `write`).
*   **Stateless:** Бекенд (Resource Server) не зберігає сесію в БД. Він лише перевіряє цифровий підпис JWT-токена публічним ключем.
*   **PKCE:** Захист для публічних клієнтів (SPA/Mobile), що замінює `client_secret` динамічним ключем для запобігання перехопленню коду.
*   **Grant Types:**
    *   `authorization_code`: Стандарт для Web/SPA (безпечний обмін через код).
    *   `client_credentials`: Для Service-to-Service зв'язку (без участі користувача).

---

### 🔄 Стандартний ланцюжок (Auth Code Flow)
1. **Redirect:** Клієнт відправляє юзера на Auth Server.
2. **Auth:** Юзер вводить пароль на стороні Auth Server.
3. **Code:** Auth Server повертає тимчасовий `code` клієнту.
4. **Exchange:** Клієнт обмінює `code` на `Access Token` (через Back-channel).
5. **Access:** Клієнт йде на Resource Server з заголовком `Authorization: Bearer <token>`.