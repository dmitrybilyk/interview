# Database Indexing Fundamentals

### 1. Clustered Index
* **Behavior:** Determines the physical storage order of data on the disk.
* **MySQL (InnoDB):** Created automatically for the **Primary Key**.
* **PostgreSQL:** No automatic clustering. Tables are "Heaps" (unordered). 
The `CLUSTER` command is a one-time manual operation.

### 2. Non-Clustered Indexes (Manual)
Most SQL indexes use the **B-Tree** (Balanced Tree) structure, providing $O(\log n)$ lookup performance.

* **Composite Index:** Built on multiple columns (e.g., `last_name, first_name`).
    * **Leftmost Prefix Rule:** The index works for `last_name` or `last_name + first_name`, 
    * but is **ignored** if you search only by `first_name`.
* **Index-Only Scan (Covering Index):** The index contains all the data needed for the query.
    * **Optimization:** The DB skips the table (Heap) entirely. 
    * In Postgres, use the `INCLUDE` clause to add "payload" columns to a search key.
* **Partial Index:** An index created on a subset of data using a `WHERE` clause.
    * **Use Case:** `CREATE INDEX ... WHERE is_active = true;` (Reduces index size and update overhead).

### 3. When to Avoid Indexes
* **Low Cardinality:** If there is poor diversity in values (e.g., `status`, `gender`), 
the DB optimizer will likely ignore the index in favor of a **Sequential Scan**.
* **High Write Volume:** Every index slows down `INSERT`, `UPDATE`, and `DELETE` 
as the tree must be rebalanced and updated.
* **Small Tables:** For tables with only a few hundred rows, 
reading the whole table into memory is faster than navigating an index tree.

### 4. Advanced Postgres Index Types
* **GIN:** Best for searching inside **JSONB** or **Arrays**.
* **GiST:** Optimized for **Geographic coordinates** or **Time ranges** (prevents overlaps).
* **BRIN:** Designed for **massive tables** (millions of rows) where data is naturally sorted, 
such as log timestamps.