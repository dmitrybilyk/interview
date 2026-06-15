# A01:2021 – Broken Access Control

---

## The moment it happens

Alice logs in and goes to her order page. The browser shows:
```
https://example.com/orders/1001
```

She sees her order. Then she manually changes the URL to:
```
https://example.com/orders/1002
```
and hits Enter.

The server runs:
```java
Order order = orderRepo.findById(orderId);  // orderId = 1002
return order;
```

It finds order 1002 — which belongs to Bob — and returns it to Alice.
The server only checked "is Alice logged in?" — it never checked "does order 1002 belong to Alice?"

**The moment**: the server fetches a resource using an ID from the URL without checking ownership.
Alice didn't hack anything. She just changed a number.

---

## Fix

```java
String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
Order order = orderRepo.findByIdAndOwner(orderId, currentUser);
// if order 1002 doesn't belong to Alice → returns empty → 404
```

The DB query now has two conditions. The ID alone is not enough — the owner must match.
Even if Alice guesses every order ID in the system, she only ever gets her own data back.

---

## Second scenario: hidden admin endpoint

A "Delete User" button is hidden from regular users in the UI.
But the endpoint still exists on the server:

```
DELETE /api/admin/users/99
```

An attacker opens browser DevTools, finds the endpoint in the JS bundle, calls it directly.
The server processes it because no role check exists — the UI just hid the button.

**The moment**: the server executes an action based on what the client sends, without verifying the caller's role.

### Fix

```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/api/admin/users/{id}")
public void deleteUser(@PathVariable Long id) { ... }
```

If Alice calls this endpoint, Spring Security rejects it before the method body runs.
Hiding something in the UI is not security — the server must enforce it.
