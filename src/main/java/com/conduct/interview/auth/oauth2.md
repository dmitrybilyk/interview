OAuth2 — це стандарт авторизації, який дозволяє одному додатку отримати обмежений доступ до захищених ресурсів 
користувача без передачі пароля. Пароль перевіряє Authorization Server і видає токен, а сам ресурс віддає 
окремий Resource Server, який цей токен перевіряє.

# OAuth2 / OIDC — Backend-controlled flow

## Учасники

| Компонент | OAuth роль | Чому |
| :--- | :--- | :--- |
| User | Resource Owner | Власник даних, логіниться на Keycloak. |
| Keycloak | Authorization Server | Логінить юзера, видає `code` і токени. |
| Frontend (Browser) | Client — публічна частина | Ініціює authorization request, редіректить на Keycloak з `client_id` (крок 2). Не має `client_secret`. |
| Backend | Client — confidential частина | Тримає `client_secret`, обмінює `code` на токен (крок 6). |
| Backend | Resource Server | Приймає сесійну cookie від Frontend і віддає `/api/data`. |

Frontend і Backend разом складають одну OAuth-роль **Client**, зареєстровану в Keycloak під одним `client_id` 
(тип — confidential client, бо секрет тримає backend). Окремо Backend додатково виступає Resource Server-ом для Frontend.

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