# A02:2021 – Cryptographic Failures

---

## Scenario 1: passwords stored with MD5

A developer stores passwords like this:
```java
String hash = DigestUtils.md5Hex(password); // "mysecret" → "5f4dcc3b5aa765d61d8327deb882cf99"
```

Six months later, the database is dumped — via SQL injection, a misconfigured backup,
or a disgruntled employee. The attacker takes the hashes and goes to
`crackstation.net`, pastes them in. In under a second, the site returns:

```
5f4dcc3b5aa765d61d8327deb882cf99 → password
```

MD5 was precomputed for every common word years ago. The attacker now has
plain-text passwords for 80% of your users — and since people reuse passwords,
those credentials work on Gmail, PayPal, and banking sites too.

**The moment**: when you stored the hash. By the time the DB is dumped it's already too late.

### Fix

```java
PasswordEncoder encoder = new BCryptPasswordEncoder(12);
String hash = encoder.encode(password);
```

BCrypt is intentionally slow (the `12` means 2^12 = 4096 rounds of hashing).
Cracking one password takes ~1 second on modern hardware. Cracking a million takes 30 years.
There is no precomputed rainbow table for BCrypt because the salt is unique per password.

---

## Scenario 2: sensitive data sent over HTTP

A login form posts to `http://` (not `https://`):
```
POST http://example.com/login
username=alice&password=mysecret
```

An attacker on the same coffee shop Wi-Fi runs Wireshark.
They see every byte of this request in plain text. Username, password, session cookie — all visible.

**The moment**: the TCP packet leaves Alice's laptop. Before it reaches your server,
it passes through routers the attacker controls. Without TLS, it's a postcard, not a sealed envelope.

### Fix

```yaml
# application.yml
server:
  ssl:
    enabled: true
```

```
Strict-Transport-Security: max-age=31536000; includeSubDomains
```

HSTS tells the browser: "never connect to this site over HTTP, even if the user types http://".
This closes the window where an attacker could intercept the very first request.

---

## Scenario 3: secret key hard-coded in source

```java
private static final String SECRET = "mySecretKey123";
Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(SECRET.getBytes(), "AES"));
```

An attacker downloads your open-source JAR, decompiles it with IntelliJ, and searches for "Key".
They find `mySecretKey123`. Now they can decrypt every piece of data your app ever encrypted.

**The moment**: when you committed the key to git. It's now in git history forever,
even if you delete it later. Anyone who ever cloned the repo has it.

### Fix

```java
String secret = System.getenv("ENCRYPTION_KEY"); // from environment / secrets manager
```

The key lives outside the code. Developers don't know the production key.
Rotating it doesn't require a code deploy.
