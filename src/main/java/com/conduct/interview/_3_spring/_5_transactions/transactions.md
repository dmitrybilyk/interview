# Transactions

A transaction groups multiple database operations into one all-or-nothing
unit: either every operation succeeds and gets committed, or any failure
rolls back everything already done in that unit - the database never
ends up in a state where only half the work happened.

---

## ACID

The four properties every transaction must guarantee:

### Atomicity — "all or nothing"
Every operation in the transaction either all succeed or all roll back.
There is no partial commit.

```
Transfer $100 from A to B:
  1. Debit  A by $100
  2. Credit B by $100

If step 2 fails → step 1 is rolled back. A is not debited. DB stays consistent.
```

### Consistency — "constraints always hold"
A transaction brings the DB from one valid state to another valid state.
All defined rules (constraints, cascades, triggers) hold before and after.

```
Rule: account balance >= 0
Transfer that would make A go negative → transaction rejected.
The constraint is enforced atomically — no in-between state visible to others.
```

### Isolation — "concurrent transactions don't interfere"
Concurrent transactions behave as if they ran serially.
One transaction's in-progress changes are invisible to others (to a configurable degree).

**Isolation levels** (weakest → strongest):

| Level | Dirty Read | Non-Repeatable Read | Phantom Read |
|---|---|---|---|
| READ UNCOMMITTED | ✓ possible | ✓ possible | ✓ possible |
| READ COMMITTED | ✗ prevented | ✓ possible | ✓ possible |
| REPEATABLE READ | ✗ prevented | ✗ prevented | ✓ possible |
| SERIALIZABLE | ✗ prevented | ✗ prevented | ✗ prevented |

**Dirty read** — reading another transaction's uncommitted (potentially rolled-back) data.
**Non-repeatable read** — re-reading the same row returns different values (another tx committed between reads).
**Phantom read** — re-running the same range query returns different rows (another tx inserted/deleted).

Most databases default to **READ COMMITTED** (PostgreSQL, Oracle) or **REPEATABLE READ** (MySQL InnoDB).
Higher isolation = more locks = lower throughput. Choose the lowest level that your use case tolerates.

### Durability — "committed data survives crashes"
Once a transaction commits, its changes are permanent — even if the server crashes immediately after.
Achieved via write-ahead log (WAL): changes are written to disk log before the commit returns.

```
COMMIT → WAL flushed to disk → response "OK"
→ power cut here → on restart, WAL is replayed → data is not lost
```

---

## `@Transactional` is another AOP proxy

Exactly like [_4_aop](../_4_aop): `<tx:annotation-driven/>` registers a
`BeanFactoryTransactionAttributeSourceAdvisor`, which the same
auto-proxying `BeanPostProcessor` uses to wrap any bean with a
`@Transactional` method in a proxy. The proxy's advice
(`TransactionInterceptor`) does, roughly:

```
begin transaction (via the configured PlatformTransactionManager)
try {
    result = realMethod.invoke(...)
    commit
    return result
} catch (RuntimeException e) {
    rollback
    throw e
}
```

Two consequences that trip people up in interviews:
- **Self-invocation doesn't get a transaction.** Calling a
  `@Transactional` method from another method on the *same* object is a
  plain Java call - it never goes through the proxy.
- **Only unchecked exceptions roll back by default.** A checked
  exception commits unless you say
  `@Transactional(rollbackFor = Exception.class)`.

---

## Propagation and isolation

- **Propagation** - how a `@Transactional` method behaves when called
  while a transaction is already active: `REQUIRED` (default: join it),
  `REQUIRES_NEW` (suspend it, start a fresh one), `NESTED` (a savepoint
  inside it, can roll back independently), `MANDATORY`/`NEVER`/`SUPPORTS`/
  `NOT_SUPPORTED` (assert or ignore, rather than start one).
- **Isolation** - how much one transaction can see of another's
  in-flight changes: `READ_UNCOMMITTED` (dirty reads allowed) <
  `READ_COMMITTED` < `REPEATABLE_READ` < `SERIALIZABLE` (strictest,
  slowest). `DEFAULT` just uses whatever the underlying database defaults to.

```java
@Transactional(
    propagation = Propagation.REQUIRES_NEW,  // always new tx, suspend current
    isolation   = Isolation.REPEATABLE_READ, // no non-repeatable reads
    rollbackFor = Exception.class            // rollback on checked exceptions too
)
public void transfer(long from, long to, BigDecimal amount) { ... }
```

---

## Run it

`TransactionsDemo` uses a plain `JdbcTemplate` against an in-memory H2
`accounts` table (no entity/JPA involved - see
[_12_spring_data_jpa](../_12_spring_data_jpa) for that layer):

1. A normal transfer succeeds; both balances update together.
2. A transfer for a suspiciously large amount throws *after* debiting
   the source account but *before* crediting the destination -
   `@Transactional` rolls both operations back, so the source balance is
   untouched.
3. The same two raw JDBC calls, run through a plain (non-`@Transactional`)
   method, leave the source account debited with nothing credited back -
   a real, persisted inconsistency - demonstrating exactly what
   `@Transactional` was preventing in step 2.

---

## Interview Points

- ACID is a property of the DB + transaction together, not just the DB.
- Atomicity is enforced by the transaction manager (undo log / rollback segments).
- Durability is enforced by WAL — commit only returns after log is flushed to disk.
- Isolation level is a trade-off: higher isolation = more locks = lower throughput.
- `READ COMMITTED` is the pragmatic default for most web apps (good enough, fast).
- `SERIALIZABLE` is rarely used in practice — optimistic locking is usually preferred.
- `@Transactional` on a `private` method does nothing — proxy can't intercept it.
- `@Transactional` on a class applies to all public methods (use with care).
