# Backend-controlled (BFF) OAuth2 flow — покроково, руками

Незалежний демо-модуль: своя Keycloak realm, свої порти, нічого спільного з `../oauth2`.
Реалізує `../oauth2/puml/BFF(client).puml` без автоматики Spring Security `oauth2Login()` —
кожен HTTP-виклик (redirect, обмін code на токен, виклик Resource Server) написаний руками
в `bff-app/.../FlowController.java` і показується на екрані як він є.

## Запуск

```bash
docker compose up -d                      # Keycloak на :8181, realm імпортується автоматично
./gradlew :resource-server-app:bootRun    # окремий термінал, порт :8189
./gradlew :bff-app:bootRun                # окремий термінал, порт :8188
```

Відкрити http://localhost:8188 і проходити кроки кнопками.

Тестовий користувач Keycloak: `alice` / `alice123`.

## Ролі в цьому демо

| Компонент | Порт | OAuth роль |
| :--- | :--- | :--- |
| Браузер | — | User Agent (тільки cookie `JSESSIONID`, токенів не бачить) |
| Keycloak | 8181 | Authorization Server |
| `bff-app` | 8188 | Client (до Keycloak) + Resource Server (для браузера) |
| `resource-server-app` | 8189 | Resource Server (для `bff-app`, який тут — Client) |
