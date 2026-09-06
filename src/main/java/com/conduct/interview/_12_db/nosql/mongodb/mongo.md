# MongoDB — Deep Dive

---

## What MongoDB is
Document-oriented NoSQL database. Stores data as **BSON** (Binary JSON) documents in **Collections**.
No fixed schema — each document can have different fields.

```
Database → Collection (≈ table) → Document (≈ row, JSON object, max 16 MB)
```

---

## Data Modeling: Embedding vs Referencing

The core design decision. Choose based on **how the data is queried**.

### Embedding (denormalization)
Store related data nested inside the parent document.
```json
{
  "_id": "order123",
  "customer": { "name": "Alice", "email": "alice@mail.com" },
  "items": [
    { "productId": "p1", "qty": 2, "price": 9.99 },
    { "productId": "p2", "qty": 1, "price": 24.99 }
  ]
}
```
- **Pro:** Single read/write — no joins. Atomic update on the document.
- **Con:** 16 MB document limit. Data duplication. Unbounded arrays grow the document indefinitely.
- **Use when:** 1:1 or bounded 1:N (order → line items, user → addresses).

### Referencing (normalization)
Store ObjectId references, like a foreign key.
```json
{ "_id": "user123", "name": "Alice" }
{ "_id": "post456", "author_id": "user123", "title": "Hello" }
```
Use `$lookup` (aggregation) to join at query time.
- **Pro:** No size limit. Independent updates to each entity.
- **Con:** Multiple reads or `$lookup` (slower than embedding).
- **Use when:** M:N relationships, large/unbounded collections (logs, events), independent lifecycle.

> **Interview trap:** unbounded embedded arrays eventually hit 16 MB → document growth → WiredTiger must relocate the document on every write. Always reference for unbounded data.

---

## Indexes

Without an index → **COLLSCAN** (full collection scan). Goal: **IXSCAN**.

```javascript
db.orders.createIndex({ customer_id: 1 })                        // single field
db.orders.createIndex({ status: 1, created_at: -1 })             // compound
db.products.createIndex({ description: "text" })                  // full-text
db.locations.createIndex({ coords: "2dsphere" })                  // geospatial
db.orders.createIndex({ status: 1 }, { partialFilterExpression: { status: "pending" } }) // partial
db.sessions.createIndex({ created_at: 1 }, { expireAfterSeconds: 3600 }) // TTL auto-delete
```

### Compound index — ESR rule
Field order in compound index: **E**quality → **S**ort → **R**ange.
```javascript
// query: status = "shipped" AND created_at > lastMonth, sort by created_at
db.orders.createIndex({ status: 1, created_at: 1 })  // equality first, then sort/range
```

### Covered query (gold standard)
All query fields AND all projected fields are in the index — no document access needed.
```javascript
db.users.createIndex({ email: 1, name: 1 })
db.users.find({ email: "a@b.com" }, { name: 1, _id: 0 })  // index-only, no doc fetch
```

### Diagnose with explain
```javascript
db.orders.find({ customer_id: "c1" }).explain("executionStats")
// look for: winningPlan.stage = "IXSCAN", totalDocsExamined ≈ nReturned
```

---

## Aggregation Pipeline

MongoDB's answer to SQL `GROUP BY`, `JOIN`, `HAVING`. Stages process documents sequentially.

```javascript
db.orders.aggregate([
  { $match: { status: "shipped" } },          // 1. filter first (uses index)
  { $group: {
      _id: "$customer_id",
      total: { $sum: "$amount" },
      count: { $sum: 1 }
  }},
  { $sort: { total: -1 } },                   // 4. sort after grouping
  { $limit: 10 },
  { $lookup: {                                // join customers collection
      from: "customers",
      localField: "_id",
      foreignField: "_id",
      as: "customer"
  }},
  { $project: { customer: 1, total: 1 } }    // shape output
])
```

**Key stages:**
| Stage | SQL equivalent |
|---|---|
| `$match` | WHERE |
| `$group` | GROUP BY + aggregates |
| `$project` | SELECT (include/exclude/compute fields) |
| `$sort` | ORDER BY |
| `$limit` / `$skip` | LIMIT / OFFSET |
| `$lookup` | LEFT JOIN |
| `$unwind` | Flatten an array (one doc per element) |
| `$addFields` | Compute new fields |

Always `$match` **early** to reduce dataset before expensive stages.

---

## Replication — Replica Set

Minimum production setup: **3 nodes** (1 Primary + 2 Secondaries).

```
Write → Primary ──oplog──► Secondary 1
                  ──oplog──► Secondary 2

If Primary fails → election among secondaries → new Primary elected in ~10s
```

**Write Concern** — controls durability:
- `w: 1` — Primary acknowledges. Fast. Risk: data loss if Primary crashes before replication.
- `w: "majority"` — majority of nodes write to journal. Safe, slightly slower. Use in production.

**Read Preference** — controls routing:
- `primary` (default) — always read from Primary (strongly consistent).
- `secondary` — reads distributed to secondaries (eventual consistency, may be slightly stale).
- Use `secondary` only when you can tolerate stale data (analytics, reporting).

---

## Sharding — Horizontal Scale

Splits a collection across multiple shards (servers) when data exceeds single-server capacity.

```
mongos (router) → shard 1 (chunk range A–M)
               → shard 2 (chunk range N–Z)
```

**Shard key** choice is critical:
- High cardinality (many distinct values) — avoid status, boolean.
- Even write distribution — avoid monotonically increasing values (like auto-increment IDs or timestamps) → all writes go to the last shard (hotspot).
- Good choice: `{ user_id: 1 }` (hashed) or `{ region: 1, user_id: 1 }` (compound).

```javascript
sh.shardCollection("mydb.orders", { user_id: "hashed" })
```

---

## Multi-Document Transactions

Available since MongoDB 4.0. Full ACID across multiple documents/collections.

```javascript
const session = client.startSession();
session.startTransaction();
try {
    db.accounts.updateOne({ _id: "A" }, { $inc: { balance: -100 } }, { session });
    db.accounts.updateOne({ _id: "B" }, { $inc: { balance:  100 } }, { session });
    await session.commitTransaction();
} catch (e) {
    await session.abortTransaction();
}
```

> Use sparingly. MongoDB's strength is single-document atomicity via embedding. Transactions add locking overhead. If your data model requires frequent multi-document transactions, reconsider the schema.

---

## Java Integration (Spring Data MongoDB)

```java
@Document(collection = "orders")
public class Order {
    @Id private String id;
    private String customerId;
    private List<OrderItem> items;   // embedded
    private OrderStatus status;
    private Instant createdAt;
}

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByCustomerIdAndStatus(String customerId, OrderStatus status);

    @Aggregation(pipeline = {
        "{ $match: { status: 'SHIPPED' } }",
        "{ $group: { _id: '$customerId', total: { $sum: '$amount' } } }"
    })
    List<CustomerTotal> totalByCustomer();
}

// MongoTemplate for complex queries
mongoTemplate.find(
    Query.query(Criteria.where("status").is("pending")
                        .and("createdAt").lt(cutoff)),
    Order.class
);
```

**application.yml:**
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/mydb
      # or: mongodb+srv://user:pass@cluster.mongodb.net/mydb (Atlas)
```

---

## Interview Points

- Document max size = **16 MB**. Use GridFS for larger files (stores chunks).
- `_id` is always unique and indexed. Default is ObjectId (12 bytes, contains timestamp).
- MongoDB is NOT schema-less — it's **schema-flexible**. Enforce schema with `$jsonSchema` validator.
- **No foreign keys** — referential integrity is the application's responsibility.
- `$lookup` is expensive on sharded collections — joins across shards require coordinator round trips.
- Oplog (operations log) is the replication backbone — also used by change streams.
- **Change Streams** — real-time watch on collection/db changes. Backed by oplog. Use for CDC, real-time notifications.
- Atlas = MongoDB's managed cloud offering (like RDS for Postgres). Has built-in search (Lucene), vector search, charts.
