# Transactions

A transaction groups multiple database operations into one all-or-nothing
unit: either every operation succeeds and gets committed, or any failure
rolls back everything already done in that unit - the database never
ends up in a state where only half the work happened.

**ACID**: Atomicity (all or nothing), Consistency (constraints always
hold), Isolation (concurrent transactions don't see each other's
half-finished work), Durability (once committed, survives a crash).

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
