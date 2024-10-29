# Spring Boot Transactions

Spring Boot provides robust transaction management, simplifying database operations
with the `@Transactional` annotation.

## Key Concepts

- **Transaction Propagation**: Defines how transactions behave across method calls.
    - `REQUIRED`: Joins an existing transaction or creates a new one (default).
    - `REQUIRES_NEW`: Suspends any existing transaction, always creating a new one.
    - `SUPPORTS`: Runs within a transaction if one exists; doesn’t start a new one.
    - `NOT_SUPPORTED`: Executes without a transaction, suspending any existing one.
    - `MANDATORY`: Requires an existing transaction, otherwise throws an exception.
    - `NEVER`: Ensures no transaction exists, throws an error if one is present.
    - `NESTED`: Runs within a nested transaction, allowing partial rollbacks.

- **Isolation Levels**: Defines how transactions isolate data access.
    - `DEFAULT`: Uses the default database isolation level.
    - `READ_COMMITTED`: Prevents dirty reads.
    - `READ_UNCOMMITTED`: Allows dirty reads.
    - `REPEATABLE_READ`: Prevents non-repeatable reads.
    - `SERIALIZABLE`: Ensures full isolation across all transactions.

## Transactional Annotation

Use `@Transactional` to demarcate transaction boundaries. It can be applied at
either the class or method level.

```java
@Transactional
public void saveData(DataEntity entity) {
    repository.save(entity);
}
