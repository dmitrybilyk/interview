# DynamoDB — Managed NoSQL

## What it is
Fully managed serverless key-value and document database.
Single-digit millisecond latency at any scale. No schema — items can have different attributes.

## Data model
- **Table** → **Items** (rows) → **Attributes** (fields, any type)
- **Partition key (PK)** — required. Determines which physical partition stores the item. Must be unique (if no sort key).
- **Sort key (SK)** — optional. Together with PK forms composite primary key. Items with same PK sorted by SK. Enables range queries.

```
PK: userId | SK: orderId#2024-01 | status: shipped | total: 99.99
PK: userId | SK: orderId#2024-02 | status: pending | total: 49.00
```

## Indexes
| Index | Different PK? | Consistency | Extra cost? |
|---|---|---|---|
| **GSI** (Global Secondary Index) | Yes (any attribute) | Eventually | Yes — own throughput |
| **LSI** (Local Secondary Index) | No (same PK) | Strong or eventual | Shares table throughput |
| LSI must be defined at table creation. GSI can be added later.

## Consistency
- **Eventually consistent read** (default) — cheaper, may lag by milliseconds.
- **Strongly consistent read** — always latest data, costs 2× read units.
- All writes are strongly consistent.
- **Transactions** (`TransactWriteItems`) — all-or-nothing across up to 100 items.

## Capacity modes
| Mode | When to use |
|---|---|
| **On-demand** | Unpredictable traffic. Pay per request. No planning needed. |
| **Provisioned** | Predictable traffic. Set RCU/WCU. Cheaper at steady load. Auto Scaling adjusts capacity. |

RCU (Read Capacity Unit) = 1 strongly consistent 4 KB read/s (or 2 eventually consistent).
WCU (Write Capacity Unit) = 1 write up to 1 KB/s.

## DynamoDB Streams
- Ordered log of all item changes (INSERT, MODIFY, REMOVE).
- Use to trigger Lambda for CDC (change data capture), replication, audit, search indexing.
- Retention: 24 hours.

## TTL (Time to Live)
Add a `ttl` attribute (Unix epoch seconds). DynamoDB auto-deletes expired items within ~48 h.
Great for sessions, cache, temporary tokens. No WCU consumed for TTL deletes.

## Java — DynamoDB Enhanced Client
```java
@DynamoDbBean
public class Order {
    private String userId;
    private String orderId;
    private String status;

    @DynamoDbPartitionKey
    public String getUserId() { return userId; }

    @DynamoDbSortKey
    public String getOrderId() { return orderId; }
    // getters/setters...
}

DynamoDbEnhancedClient enhanced = DynamoDbEnhancedClient.builder()
    .dynamoDbClient(DynamoDbClient.create()).build();
DynamoDbTable<Order> table = enhanced.table("orders", TableSchema.fromBean(Order.class));

// Put
table.putItem(order);

// Get
Order found = table.getItem(r -> r.key(k -> k.partitionValue("u1").sortValue("o1")));

// Query (all orders for user u1 starting with 2024)
table.query(r -> r.keyConditionExpression("userId = :u AND begins_with(orderId, :prefix)")
                  .expressionAttributeValues(Map.of(":u", av("u1"), ":prefix", av("2024"))));
```

## Interview points
- Hot partition problem: if all reads/writes go to one PK value, you hit a throughput ceiling. Design PK for even distribution.
- DynamoDB is NOT for complex joins — denormalize and duplicate data intentionally.
- Max item size: 400 KB. For bigger objects, store in S3 and keep the S3 key in DynamoDB.
- Global Tables = multi-region active-active replication, eventual consistency across regions.
- DynamoDB vs MongoDB: DynamoDB is serverless + AWS-native, no ops; MongoDB is richer query language.
