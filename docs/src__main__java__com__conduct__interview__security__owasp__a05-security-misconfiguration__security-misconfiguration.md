# A05:2021 – Security Misconfiguration

---

## Scenario 1: Spring Boot Actuator left open

A developer adds `spring-boot-actuator` to the project for local debugging.
The app ships to production. Nobody changes the default config.

An attacker scans common paths:
```
GET https://example.com/actuator/env
```

The browser returns a JSON document with every environment variable the app can see:
```json
{
  "DATABASE_URL": "postgresql://prod-db:5432/myapp",
  "DATABASE_PASSWORD": "s3cr3tProdPass!",
  "AWS_SECRET_ACCESS_KEY": "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"
}
```

The attacker now has the production database password and AWS keys.
They didn't exploit any code — they just opened a URL that was never meant to be public.

**The moment**: when the app was deployed without reviewing which endpoints are exposed.
The misconfiguration was there from day one.

### Fix

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health   # only expose what monitoring actually needs
```

---

## Scenario 2: verbose error messages

A user submits a form with an email that already exists. The server throws an exception.
Instead of a generic message, the browser shows:

```
org.springframework.dao.DataIntegrityViolationException:
  could not execute statement; SQL [insert into users (id, email, password_hash)];
  constraint [users_email_key]
  at com.example.app.service.UserService.register(UserService.java:47)
  at com.example.app.controller.UserController.register(UserController.java:23)
```

The attacker now knows: the framework is Spring, the DB has a table called `users`
with columns `id`, `email`, `password_hash`, and the exact file and line number where
registration logic lives. This information directly helps them craft more targeted attacks.

**The moment**: the exception propagated all the way to the HTTP response unhandled.
The server handed the attacker a map of its internals.

### Fix

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAll(Exception ex) {
        log.error("Unhandled exception", ex);  // full detail goes to your log
        return ResponseEntity.status(500)
            .body(Map.of("error", "Something went wrong")); // nothing useful to the attacker
    }
}
```

The full stack trace goes to your logging system where your team can see it.
The client gets a one-line generic message.

---

## Scenario 3: default admin credentials

A Tomcat server ships with a manager console at `/manager/html`.
Default credentials: `admin / admin`.

An attacker finds the URL via a Google search for `site:example.com /manager`.
Types `admin` / `admin`. Gets in. Uploads a `.war` file containing a web shell.
Now has full remote code execution on your server.

**The moment**: deployment. Nobody changed the defaults.

### Fix

- Change all default credentials before first deployment.
- Block the admin console from the public internet entirely (firewall / VPN only).
- If you don't use a feature, remove it or disable it.

---

## Scenario 4: missing security headers

No `X-Frame-Options` header on the login page.

An attacker creates a page that embeds your login page in a hidden `<iframe>`,
positioned under a "Click here to win a prize!" button. The victim thinks they're clicking
the prize button but they're actually clicking the login form underneath — submitting
their credentials to your server while the attacker observes.
This is called **clickjacking**.

### Fix

```java
http.headers(h -> h.frameOptions().deny());
```

One line. The browser now refuses to render your page inside any iframe.
