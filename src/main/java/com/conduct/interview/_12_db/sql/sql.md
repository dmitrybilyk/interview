# SQL — Interview Reference

---

## ACID

| Property | Meaning |
|---|---|
| **Atomicity** | Transaction is all-or-nothing. If any step fails, the whole thing rolls back. |
| **Consistency** | Data moves from one valid state to another. Constraints, FKs, and rules are never violated. |
| **Isolation** | Concurrent transactions don't see each other's intermediate state. |
| **Durability** | Committed data survives crashes (written to WAL/disk before commit is confirmed). |

---

## Transaction Isolation Levels

Higher isolation = fewer anomalies, more locking, less concurrency.

| Level | Dirty Read | Non-Repeatable Read | Phantom Read |
|---|---|---|---|
| READ UNCOMMITTED | ✅ possible | ✅ possible | ✅ possible |
| READ COMMITTED | ❌ prevented | ✅ possible | ✅ possible |
| REPEATABLE READ | ❌ | ❌ prevented | ✅ possible |
| SERIALIZABLE | ❌ | ❌ | ❌ prevented |

- **Dirty read** — read uncommitted data from another transaction (it may roll back).
- **Non-repeatable read** — same row read twice in one transaction returns different values (another tx committed an UPDATE).
- **Phantom read** — same query run twice returns different rows (another tx committed an INSERT/DELETE).

PostgreSQL default: **READ COMMITTED**. `REPEATABLE READ` in Postgres also prevents phantoms (MVCC).

```sql
BEGIN;
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
-- ... queries ...
COMMIT;
```

---

## Normalization

Goal: eliminate redundancy and update anomalies.

### 1NF — Atomic values, no repeating groups
- Each cell holds exactly one value (no comma-separated lists, no arrays as plain text).
- Each row is uniquely identifiable (has a primary key).

```
BAD:  student_id | courses
      1          | "Math, Physics"

GOOD: student_id | course
      1          | Math
      1          | Physics
```

### 2NF — No partial dependencies (only relevant with composite PKs)
Every non-key column must depend on the **entire** primary key, not part of it.

```
BAD composite PK (order_id, product_id):
  product_name depends only on product_id → partial dependency → split into Products table

GOOD:
  OrderItem(order_id, product_id, quantity)   ← quantity depends on both
  Product(product_id, name, price)            ← name depends only on product_id
```

### 3NF — No transitive dependencies
Non-key columns must not depend on other non-key columns.

```
BAD:  employee_id | department_id | department_name
      dept_name depends on dept_id, not on employee_id

GOOD:
  Employee(id, department_id)
  Department(id, name)
```

### BCNF
Stricter than 3NF: every determinant must be a candidate key.
Rarely needed; matters for tables with multiple overlapping candidate keys.

---

## Indexes

### How they work
B-Tree index: `O(log n)` lookup. The DB builds a sorted tree; leaf nodes hold `(key, row pointer)`.
Without an index: **Sequential Scan** — reads every row (`O(n)`).

### Types
| Index type | Use case |
|---|---|
| **B-Tree** (default) | Equality, ranges, ORDER BY, LIKE 'prefix%' |
| **Hash** | Equality only, slightly faster than B-Tree for `=` |
| **GIN** | JSONB, arrays, full-text search |
| **GiST** | Geospatial, ranges, custom types |
| **BRIN** | Very large tables with naturally ordered data (timestamps in log tables) |
| **Partial** | `WHERE status = 'active'` — index only active rows |

### Composite indexes — Leftmost prefix rule
```sql
CREATE INDEX idx ON orders (status, created_at);
-- Works:   WHERE status = 'shipped' AND created_at > '2024-01-01'
-- Works:   WHERE status = 'shipped'
-- Ignored: WHERE created_at > '2024-01-01'   (no leading column)
```

**ESR rule** for compound index column order: **E**quality first → **S**ort → **R**ange.

### Covering index (Index-Only Scan)
Index contains all columns the query needs — no heap (table) access required.
```sql
CREATE INDEX idx_cover ON orders (customer_id) INCLUDE (status, total);
-- SELECT status, total FROM orders WHERE customer_id = 5  → index only
```

### When NOT to index
- Low-cardinality columns (boolean, gender, status with 3 values) — optimizer skips them.
- Heavy write tables — every DML updates all indexes.
- Small tables — sequential scan is faster.

### EXPLAIN ANALYZE
```sql
EXPLAIN ANALYZE SELECT * FROM orders WHERE customer_id = 42;
-- Look for: Seq Scan (bad) vs Index Scan / Index Only Scan (good)
-- Check: rows, actual rows, loops, buffers
```

---

## Joins

```
employees (id, name, dept_id, manager_id, salary)
departments (id, name)
```

| Join | Returns |
|---|---|
| `INNER JOIN` | Only rows with a match in both tables |
| `LEFT JOIN` | All from left + matching from right (NULL if no match) |
| `RIGHT JOIN` | All from right + matching from left |
| `FULL OUTER JOIN` | All from both, NULL where no match |
| `CROSS JOIN` | Cartesian product (every row × every row) |
| `SELF JOIN` | Table joined to itself |

```sql
-- Employees with no department (NULL in right side of LEFT JOIN)
SELECT e.name FROM employees e
LEFT JOIN departments d ON e.dept_id = d.id
WHERE d.id IS NULL;

-- Self join: employee earns more than their manager
SELECT e.name AS emp, m.name AS mgr
FROM employees e
JOIN employees m ON e.manager_id = m.id
WHERE e.salary > m.salary;

-- Self join: worker + their manager + grand-manager
SELECT w.name, m.name AS manager, gm.name AS grand_manager
FROM workers w
JOIN workers m  ON w.manager_id = m.id
JOIN workers gm ON m.manager_id = gm.id;
```

---

## Window Functions

Run a calculation **across a set of rows related to the current row** without collapsing them (unlike GROUP BY).

```sql
SELECT
    name, dept_id, salary,
    ROW_NUMBER()  OVER (PARTITION BY dept_id ORDER BY salary DESC) AS row_num,   -- unique, gaps
    RANK()        OVER (PARTITION BY dept_id ORDER BY salary DESC) AS rnk,       -- ties share rank, gaps
    DENSE_RANK()  OVER (PARTITION BY dept_id ORDER BY salary DESC) AS dense_rnk, -- ties share rank, no gaps
    LAG(salary)   OVER (PARTITION BY dept_id ORDER BY salary)      AS prev_sal,  -- previous row value
    LEAD(salary)  OVER (PARTITION BY dept_id ORDER BY salary)      AS next_sal,  -- next row value
    SUM(salary)   OVER (PARTITION BY dept_id)                      AS dept_total
FROM employees;
```

### Top N per group (classic interview question)
```sql
-- Top 3 earners per department (DENSE_RANK handles ties)
SELECT * FROM (
    SELECT e.*,
           DENSE_RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) AS dr
    FROM employees e
) ranked
WHERE dr <= 3;
```

---

## Subqueries & CTEs

### When subqueries hurt: correlated subquery
Runs once per outer row — `O(n²)` — avoid for large tables.
```sql
-- BAD: for every student, counts enrollments separately
SELECT s.name FROM students s
WHERE 50 > (SELECT COUNT(*) FROM enrollments e WHERE e.student_id = s.student_id);

-- GOOD: one pass with JOIN + GROUP BY
SELECT s.name FROM students s
JOIN (SELECT student_id, COUNT(*) cnt FROM enrollments GROUP BY student_id) e
     ON s.student_id = e.student_id
WHERE e.cnt < 50;
```

### JOIN vs subquery — when each wins
| | JOIN | Subquery |
|---|---|---|
| Readability for simple filters | Good | Fine for `IN` |
| Correlated filter | Use JOIN + GROUP | Avoid |
| EXISTS check | `EXISTS` subquery | Good fit |
| Planner | Usually same execution plan | Sometimes worse |

### CTE (Common Table Expression)
```sql
WITH worker_totals AS (
    SELECT worker_id, SUM(amount) AS total FROM earnings GROUP BY worker_id
),
company_avg AS (
    SELECT AVG(total) AS avg FROM worker_totals
)
SELECT w.name, wt.total
FROM workers w
JOIN worker_totals wt ON w.id = wt.worker_id
CROSS JOIN company_avg ca
WHERE wt.total > ca.avg;
```
CTEs are readable. In PostgreSQL pre-12 they were optimization fences (always materialized); since PG 12 the planner can inline them.

---

## Common Interview Query Patterns

### Department with highest average salary
```sql
SELECT d.name, AVG(e.salary) AS avg_sal
FROM departments d
JOIN employees e ON d.id = e.dept_id
GROUP BY d.id, d.name
ORDER BY avg_sal DESC
LIMIT 1;
```

### Employees above their department's average
```sql
SELECT e.name, e.salary, d.name
FROM employees e
JOIN departments d ON e.dept_id = d.id
WHERE e.salary > (SELECT AVG(salary) FROM employees WHERE dept_id = e.dept_id);
```

### Most recent order per customer
```sql
WITH ranked AS (
    SELECT c.name, o.order_date, o.total,
           ROW_NUMBER() OVER (PARTITION BY c.id ORDER BY o.order_date DESC) AS rn
    FROM customers c JOIN orders o ON c.id = o.customer_id
)
SELECT * FROM ranked WHERE rn = 1;
```

### Customer segmentation with CASE
```sql
SELECT c.name,
       SUM(o.total) AS spend,
       CASE WHEN SUM(o.total) > 300 THEN 'VIP'
            WHEN SUM(o.total) > 100 THEN 'REGULAR'
            ELSE 'BUDGET' END AS segment
FROM customers c JOIN orders o ON c.id = o.customer_id
GROUP BY c.id, c.name
ORDER BY spend DESC;
```

---

## Constraints

```sql
CREATE TABLE orders (
    id         SERIAL PRIMARY KEY,                        -- NOT NULL + UNIQUE
    customer_id INT NOT NULL REFERENCES customers(id)     -- FK + NOT NULL
                    ON DELETE CASCADE,                    -- delete orders when customer deleted
    status     VARCHAR(20) NOT NULL
                    CHECK (status IN ('pending','shipped','done')),
    total      NUMERIC(10,2) CHECK (total >= 0),
    code       VARCHAR(10) UNIQUE                         -- unique nullable
);
```

- `ON DELETE CASCADE` — child rows deleted with parent.
- `ON DELETE SET NULL` — FK column set to NULL.
- `ON DELETE RESTRICT` — prevents deleting parent if children exist (default).

---

## PostgreSQL Advantages over MySQL

| Feature | PostgreSQL | MySQL |
|---|---|---|
| **Transactional DDL** | ✅ Rollback schema changes | ❌ DDL auto-commits |
| **MVCC** | Advanced — readers never block writers | Basic |
| **JSONB** | Binary, GIN-indexed, fast queries | Slower JSON |
| **Index types** | B-Tree, Hash, GIN, GiST, BRIN, partial, expression | Mainly B-Tree |
| **Arrays** | Native `TEXT[]`, `INT[]` etc. | No |
| **Extensions** | PostGIS, pgvector, pg_trgm, TimescaleDB | Limited |
| **Materialized Views** | Yes | No |
| **Data integrity** | Strict CHECK constraints enforced | Less strict |
| **Window functions** | Full | Limited |

---

## Performance Tips

- Index columns used in `WHERE`, `JOIN ON`, `ORDER BY`.
- Index foreign keys — joins and cascades scan them.
- Use `EXPLAIN ANALYZE` in development. Target: `Index Scan` not `Seq Scan` on large tables.
- Avoid `SELECT *` — fetch only needed columns (especially with wide rows or JSONB).
- Avoid `OFFSET` for pagination on large tables — use keyset pagination (`WHERE id > last_seen_id`).
- Connection pooling (PgBouncer / HikariCP) — DB connections are expensive.
- Materialized view for expensive aggregates refreshed periodically.
- Partial indexes for filtered datasets (`WHERE deleted_at IS NULL`).
- `VACUUM ANALYZE` to keep planner statistics fresh.

---

## Run Postgres locally (Docker)
```bash
docker run --name pg-container \
  -e POSTGRES_USER=test -e POSTGRES_PASSWORD=test -e POSTGRES_DB=testdb \
  -p 5432:5432 -d postgres:15
```
