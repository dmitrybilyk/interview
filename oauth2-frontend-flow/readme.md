# Frontend-controlled OAuth2 flow (SPA + PKCE) — покроково, руками

Незалежний демо-модуль: своя Keycloak realm, свої порти, нічого спільного з `../oauth2` чи
`../oauth2-backend-flow`. Реалізує `../oauth2/puml/SPA-FLOW(Server).puml`: увесь authorization_code
+ PKCE флоу виконує чистий vanilla JS у браузері (`spa-app/.../static/*.js`), без жодного секрету
і без бекенд-логіки взагалі.

## Запуск

```bash
docker compose up -d                      # Keycloak на :8281, realm імпортується автоматично
./gradlew :resource-server-app:bootRun    # окремий термінал, порт :8289
./gradlew :spa-app:bootRun                # окремий термінал, порт :8288
```

Відкрити http://localhost:8288 і проходити кроки кнопками. Слідкуй за DevTools → Network — там теж
видно всі ці запити, бо тут браузер ходить у Keycloak і в Resource Server напряму.

Тестовий користувач Keycloak: `alice` / `alice123`.

## Ролі в цьому демо

| Компонент | Порт | OAuth роль |
| :--- | :--- | :--- |
| Браузер (JS в `spa-app`) | 8288 | Client (public, PKCE, без secret) |
| Keycloak | 8281 | Authorization Server |
| `resource-server-app` | 8289 | Resource Server |

`spa-app` сам по собі не має OAuth-ролі — він лише роздає статичні файли.
