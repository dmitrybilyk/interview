# A04:2021 – Insecure Design

---

## Scenario 1: predictable password reset token

Alice clicks "Forgot password". The server generates a reset link:
```
https://example.com/reset?token=42_1718000000
```

`42` is Alice's user ID. `1718000000` is the current Unix timestamp. Both are predictable.

An attacker wants to reset Alice's password. They know her user ID (from a public profile URL).
They request a reset for their own account at the same moment, check what timestamp was used,
and construct Alice's token themselves — without ever receiving an email.

```
https://example.com/reset?token=42_1718000042
```

They open that URL, set a new password for Alice. Done.

**The moment**: when the developer chose to build the token from known values instead of
generating a random secret. The flaw exists before any attacker shows up — it's in the design.

### Fix

```java
byte[] bytes = new byte[32];
new SecureRandom().nextBytes(bytes);
String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes); // 256 bits of randomness
```

Store `hash(token)` in DB, linked to the user. Expire it in 15 minutes.
An attacker cannot guess or compute a 256-bit random value.

---

## Scenario 2: no rate limiting on OTP

A user logs in with a 6-digit SMS code. There are 1,000,000 possible codes.
The server accepts guesses with no limit.

An attacker writes a script:
```python
for code in range(1000000):
    r = requests.post('/verify-otp', json={'otp': f'{code:06d}'})
    if r.status_code == 200:
        print('Found:', code)
        break
```

At 100 requests/second this takes ~3 hours. The attacker bypasses MFA entirely.

**The moment**: the designer never asked "what happens if someone calls this endpoint
a million times?" — because in normal use, nobody does.

### Fix

Lock the account after 5 wrong OTP attempts. Force a new OTP to be issued.
This reduces the effective search space from 1,000,000 to 5 — making brute force impossible.

---

## Scenario 3: coupon applied client-side only

A checkout flow applies a discount in the browser:

```javascript
if (couponCode === 'SAVE20') {
    totalPrice = totalPrice * 0.8;
}
```

The server receives the final price and trusts it.

An attacker opens DevTools, changes `totalPrice` to `0.01` before the form submits.
The server charges $0.01 for a $500 order.

**The moment**: the developer trusted a value that came from the client.
The client is the attacker's own browser — they control everything in it.

### Fix

The server calculates the price independently. The client sends the order items and coupon code.
The server looks up prices from its own DB, applies the discount itself, charges that amount.
Whatever number the client sends as a price is ignored.
