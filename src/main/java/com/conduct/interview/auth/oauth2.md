OAuth2 — це стандарт авторизації, який дозволяє одному додатку (наприклад, твоєму сайту) отримати обмежений доступ 
до ресурсів користувача на іншому сервісі (наприклад, Google, GitHub, Keycloak) без передачі пароля.

# OAuth2 / OIDC — Backend-controlled flow

## Учасники
- Frontend (Browser)
- Backend
- Authorization Server (Keycloak)
- Resource Server (API)
  Resource Server — це роль сервісу, який захищає ресурси і перевіряє токени.
  У більшості випадків наш backend є resource server для frontend.
  Але той самий backend може виступати як client, коли викликає інші сервіси.

---

## Конфігурація Backend
```
client_id
client_secret
redirect_uri = https://app.com/callback
```

---

## Flow

### 1. User → Frontend
```
GET /app
```

### 2. Frontend → Keycloak (redirect)
```
302 https://keycloak/auth
  ?client_id=app-client
  &redirect_uri=https://app.com/callback
  &response_type=code
  &scope=openid
```

### 3. User login на Keycloak

### 4. Keycloak → Frontend (redirect)
```
302 https://app.com/callback?code=abc123
```

### 5. Frontend → Backend
```
POST /auth/exchange
{
  "code": "abc123"
}
```

### 6. Backend → Keycloak (/token)
```
POST /token

grant_type=authorization_code
code=abc123
client_id=app-client
client_secret=SECRET
redirect_uri=https://app.com/callback
```

### 7. Keycloak → Backend
```
access_token
refresh_token
id_token
```

### 8. Backend storage
- session / Redis / DB

### 9. Backend → Frontend
```
Set-Cookie: SESSION=xyz; HttpOnly; Secure
```

---

## Виклики API

### Frontend → Backend
```
GET /api/data
Cookie: SESSION=xyz
```

---

## Перевірка JWT

### Отримання ключів
```
GET /realms/{realm}/protocol/openid-connect/certs
```

### Перевірка
- взяти `kid` з JWT header
- знайти ключ у JWKS
- перевірити підпис
- перевірити `exp`
- перевірити `iss`
- перевірити `aud`

---

## Refresh token

### Backend → Keycloak
```
POST /token

grant_type=refresh_token
refresh_token=XYZ
client_id=app-client
client_secret=SECRET
```

### Keycloak → Backend
```
new access_token
new refresh_token
```

---

## client_secret

Використовується:
- authorization_code → token
- refresh_token → token

Не використовується:
- для перевірки JWT
- для доступу до API
```