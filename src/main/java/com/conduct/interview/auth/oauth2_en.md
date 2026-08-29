# Auth Theory: Authentication, Authorization, OAuth2, OIDC, SSO

---

## Authentication vs Authorization

### Authentication (AuthN) — "Who are you?"
Verifying identity. You prove that you are who you claim to be.

- You provide a login/password, biometrics, OTP
- The system says: "Yes, this is Alice"
- Result: **identity**

### Authorization (AuthZ) — "What are you allowed to do?"
Checking permissions. The system decides what Alice is allowed to do.

- After authentication: "Alice can read /orders, but not /admin"
- Result: **permissions / scopes / roles**

> Authentication first. Authorization second.
> You cannot authorize someone you haven't identified yet.

---

## OAuth2

**OAuth2** is an **authorization** standard (RFC 6749).

It solves one problem: how to give an application access to your resources
**without handing it your password**.

### Participants

| Role | Who | What it does |
|:--|:--|:--|
| **Resource Owner** | The user | Owner of the data, grants permission |
| **Client** | Your application | Wants access to the data |
| **Authorization Server** | Keycloak / Google Auth | Authenticates the user, issues tokens |
| **Resource Server** | API | Protected resource, validates the token |

### What OAuth2 gives you

Instead of a password — an **access_token**. A time-limited, scope-limited access key.

```
Client receives:  access_token  (for the API)
                   refresh_token (to renew the access_token)
```

### What OAuth2 does NOT give you

OAuth2 **doesn't say who the user is**. It only grants access.
For identification you need OIDC.

### Grant types (ways to obtain a token)

| Grant Type | When it's used |
|:--|:--|
| **Authorization Code** | Web app / mobile: user logs in through a browser |
| **Authorization Code + PKCE** | SPA / mobile without a backend: no `client_secret` |
| **Client Credentials** | Server-to-server: no user involved |
| **Refresh Token** | Renew the access_token without logging in again |

---

## OIDC (OpenID Connect)

**OIDC** is a layer on top of OAuth2 that adds **authentication**.

OAuth2 answers "what's allowed?", OIDC adds "who is this?".

### What OIDC adds

- **id_token** — a JWT with user data (`sub`, `email`, `name`)
- **UserInfo endpoint** — `/userinfo` for additional claims
- **scope `openid`** — mandatory, turns on OIDC on top of OAuth2

```
Request:  scope=openid profile email
Response: access_token  (OAuth2 — for the API)
          id_token      (OIDC  — who logged in)
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

- `sub` — the user's unique ID (never changes)
- `iss` — who issued the token (the Authorization Server)
- `aud` — who the token is for (your `client_id`)
- `exp` — when it expires

### OAuth2 vs OIDC — short version

| | OAuth2 | OIDC |
|:--|:--|:--|
| Purpose | Authorization | Authentication |
| Question | "What's allowed?" | "Who is this?" |
| Token | access_token | id_token (JWT) |
| Activated by | always | `scope=openid` |

---

## SSO (Single Sign-On)

**SSO** — one login grants access to multiple applications.

### How it works

1. The user logs into Keycloak → gets a **session** on Keycloak
2. Opens App2 → App2 redirects to Keycloak
3. Keycloak sees an active session → **doesn't ask for the password again**
4. Issues tokens for App2 automatically

### Where the session is stored

Keycloak keeps an **SSO session** (stored server-side + a `KEYCLOAK_SESSION` cookie in the browser).
This session is separate from your applications' own sessions.

### Logout in SSO

There are two options:

**Local logout** — only your app forgets its session. Other apps and Keycloak don't know about it.

**Global logout (Back-channel / Front-channel)** — Keycloak invalidates the SSO session and notifies every connected application.

---

## JWT — how the token is verified

The Resource Server verifies the JWT signature **without contacting the Authorization Server** (stateless).

```
1. Fetch the JWKS: GET /realms/{realm}/protocol/openid-connect/certs
2. Take the `kid` from the JWT header
3. Find the matching public key in the JWKS
4. Verify the RSA/ECDSA signature
5. Check exp (not expired)
6. Check iss (matches the expected realm)
7. Check aud (your client_id or resource server)
```

Keys are cached — an actual call to Keycloak happens rarely (only when keys rotate).

---

## Flows — quick comparison

### Backend-controlled (Authorization Code, BFF pattern)

```
Browser → BFF → Keycloak (back-channel for tokens)
Tokens are stored on the server. The browser only gets an httpOnly cookie.
```
- Secure: tokens never reach the browser
- Requires a backend

### SPA + PKCE (Authorization Code + PKCE)

```
Browser → Keycloak (code) → Browser POST /token with code_verifier
Tokens are stored in the browser (memory / sessionStorage).
```
- No `client_secret` (public client)
- PKCE replaces the secret: `code_verifier` → `code_challenge = SHA-256(verifier)`
- Tokens are accessible to JS → XSS risk

---

## Refresh token

When the `access_token` expires (usually 5 min):

```
POST /token
grant_type=refresh_token
refresh_token=...
client_id=...
client_secret=...   ← only for confidential clients
```

You get a new `access_token` (and a new `refresh_token`).
`client_secret` is only needed for exchanging tokens, **not** for verifying a JWT.

---

## Keycloak — what it is

Keycloak is an open-source Authorization Server and Identity Provider (IdP).

Implements OAuth2 + OIDC + SSO + SAML.

Key concepts:
- **Realm** — an isolated area (a separate tenant). Its own user DB, its own clients.
- **Client** — a registered application (`client_id` + `client_secret`)
- **Scope** — what the client is asking permission for (`openid`, `profile`, `email`, custom ones)
- **Role** — a user's role (goes into the token as a claim)
