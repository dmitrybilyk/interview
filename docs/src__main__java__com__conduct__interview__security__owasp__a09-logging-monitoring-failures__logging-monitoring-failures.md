# A09:2021 – Security Logging and Monitoring Failures

---

## The moment it happens (or rather — doesn't happen)

An attacker discovers that your `/api/users/{id}` endpoint returns user profile data
including name, email, and phone number. They write a script:

```python
for user_id in range(1, 100000):
    r = requests.get(f'https://example.com/api/users/{user_id}',
                     headers={'Authorization': f'Bearer {stolen_token}'})
    if r.status_code == 200:
        save(r.json())
```

They run it over two hours. They collect 47,000 user records.

Your app returns 200 for every valid ID. No alert fires. No log entry says
"user X accessed user Y's data." The only log you have is a generic access log:

```
200 GET /api/users/1    12ms
200 GET /api/users/2    11ms
200 GET /api/users/3    13ms
...
```

Nothing looks unusual. The attacker finishes, disappears.

You find out three weeks later when a journalist emails you asking about the data
that appeared for sale on a dark web forum.

**The moment it should have happened**: when the first request hit a user ID that
didn't belong to the authenticated user. That's when an alert should have fired.

---

## What was missing and why it mattered

| What was missing | What it meant |
|---|---|
| No log of which user accessed which resource | Cannot reconstruct what was stolen |
| No alert on 1,000 requests/min from one token | Attack ran for 2 hours unnoticed |
| No anomaly detection on cross-user access | No signal during the attack |
| No log of authorization checks | Cannot tell in court what data was exposed |

---

## Fix: log the right events

```java
// When a user accesses another user's resource — log it
log.warn("CROSS_USER_ACCESS user={} accessed resource owner={} resourceId={}",
         currentUser, resourceOwner, resourceId);

// On every failed authorization
log.warn("ACCESS_DENIED user={} uri={} method={}", currentUser, uri, method);

// On login success and failure
log.info("AUTH_SUCCESS user={} ip={}", username, clientIp);
log.warn("AUTH_FAILURE user={} ip={}", username, clientIp);
```

Put context on every log line using MDC so you can trace a full request:

```java
MDC.put("correlationId", UUID.randomUUID().toString());
MDC.put("userId", currentUser);
// Every log.xxx() call in this request now includes correlationId and userId automatically
```

---

## Fix: alert on the attack pattern

Wire your logs to a SIEM (ELK, Datadog, Splunk) and create a rule:

```
IF same token makes > 500 requests/minute to /api/users/*
AND the user_id in the path is never the same as the token's owner
THEN fire alert: "Possible IDOR scraping attack"
```

This rule would have fired within the first minute of the attack.
The attacker would have been blocked and investigated — not two weeks later.

---

## What never to log

```java
// BAD — logs a password in plain text if user mistyped their email as a password
log.info("Login attempt: user={} password={}", username, password);

// BAD — logs the full credit card number
log.info("Payment for card={}", cardNumber);
```

Logs are stored long-term, often in less-secured systems, and are read by many people.
A log containing passwords or card numbers is a second sensitive data store that nobody's protecting.
