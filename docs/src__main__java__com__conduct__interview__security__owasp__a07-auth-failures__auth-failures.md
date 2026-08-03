# A07:2021 – Identification and Authentication Failures

---

## Scenario 1: credential stuffing

LinkedIn was breached in 2012. 117 million email/password pairs were leaked.
The list is freely downloadable on the dark web.

An attacker downloads it and writes a script:
```python
for email, password in leaked_pairs:
    r = requests.post('https://yourbank.com/login',
                      json={'email': email, 'password': password})
    if r.status_code == 200:
        print(f'VALID: {email} / {password}')
```

They run it overnight. 2% of your users reused their LinkedIn password on your site.
If you have 500,000 users, that's 10,000 accounts compromised — and your app did nothing wrong.
The users' passwords were correct. The attacker just happened to have them.

**The moment**: when the script hits a matching email/password pair. Your server can't
tell a legitimate login from an attacker using a stolen password — they look identical.

### Fix

The only reliable defence is to make a correct password not enough on its own:

```java
// After password check passes, require a second factor
Totp totp = new Totp(user.getTotpSecret()); // TOTP = Google Authenticator style
if (!totp.verify(userProvidedCode)) {
    throw new BadCredentialsException("Invalid OTP");
}
```

Also detect the attack in progress: if 10,000 different accounts get correct-password
login attempts from 50 IP addresses in one hour, that's credential stuffing — alert and
temporarily block those IPs.

---

## Scenario 2: no account lockout (brute force)

A bank uses a 4-digit PIN. No lockout after wrong attempts.

An attacker knows a target's account number. They write:
```python
for pin in range(10000):
    r = requests.post('/login', json={'account': '12345678', 'pin': f'{pin:04d}'})
    if r.status_code == 200:
        break  # found it — statistically within 5,000 attempts
```

At 10 requests/second: ~8 minutes to try all combinations.

**The moment**: the 10,000th request. No alarm was triggered, no slowdown, no lockout.

### Fix

```java
if (user.getFailedAttempts() >= 5) {
    throw new LockedException("Account locked. Contact support.");
}
```

After 5 wrong PINs the account is locked. The attacker's script hits the wall at attempt 6.
They can no longer brute-force — the effective search space collapses from 10,000 to 5.

---

## Scenario 3: session not regenerated after login (session fixation)

An attacker visits your site without logging in. The server assigns a session ID:
```
Set-Cookie: JSESSIONID=abc123
```

The attacker sends their victim a link with that session ID embedded.
The victim clicks it, logs in. The server associates `abc123` with the victim's account
but never issues a new session ID.

The attacker still has `abc123`. They set that cookie in their browser.
They are now authenticated as the victim — without knowing their password.

**The moment**: when the server authenticated the user but kept the same session ID
that existed before login. The attacker's pre-login session became a post-login session.

### Fix

```java
http.sessionManagement(session -> session
    .sessionFixation().newSession() // destroys old session, creates a new ID on login
);
```

After login the old `abc123` is gone. A new, unpredictable session ID is issued.
Even if the attacker had planted `abc123`, it's now worthless.
