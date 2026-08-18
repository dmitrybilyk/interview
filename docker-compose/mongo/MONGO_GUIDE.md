# MongoDB Practice Guide

## Setup

```bash
# Start container (seeds data automatically on first run)
docker compose up -d

# Connect
mongosh "mongodb://admin:admin123@localhost:27017"

# Inside mongosh
use practicedb

# Re-seed manually (drops + re-inserts everything)
mongosh "mongodb://admin:admin123@localhost:27017" --file init/01-init.js

# Wipe volume to re-trigger auto-seed
docker compose down -v && docker compose up -d
```

### Collections
| Collection | Documents | Purpose |
|---|---|---|
| `users` | 8 | nested docs, arrays, geo origin |
| `products` | 12 | categories, tags arrays, nested specs |
| `orders` | 12 | embedded items array, references userId |
| `reviews` | 12 | full-text fields, ratings |
| `stores` | 5 | GeoJSON Points for geospatial |
| `sessions` | 3 | TTL index demo |

---

## 1. CRUD

### Insert
```js
// insertOne
db.users.insertOne({
    name: "Test User", email: "test@test.com", age: 25,
    premium: false, address: { city: "Prague", country: "CZ", zip: "11000" },
    interests: ["tech"], scores: [70], createdAt: new Date()
})

// insertMany
db.products.insertMany([
    { name: "USB Hub", category: "Electronics", price: 19.99, stock: 200, tags: ["usb"] },
    { name: "Desk Lamp", category: "Electronics", price: 34.99, stock: 80, tags: ["light"] }
])
```

### Find
```js
db.users.find()                                    // all docs
db.users.findOne({ _id: 1 })                       // by _id
db.users.find({ premium: true })                   // filter
db.users.find({ age: { $gt: 30 } })               // comparison
db.users.find({ age: { $gte: 29, $lte: 40 } })   // range
db.users.find({ "address.city": "New York" })      // nested field (dot notation)
db.users.find({ interests: "tech" })               // match inside array
db.users.find({ interests: { $in: ["tech", "gaming"] } })  // any of
db.users.find({ interests: { $all: ["tech", "gaming"] } }) // must have all
db.users.find({ interests: { $size: 2 } })         // array length = 2
```

### Projection
```js
db.users.find({}, { name: 1, email: 1, _id: 0 })          // include only
db.users.find({}, { scores: 0, createdAt: 0 })             // exclude
db.users.find({ premium: true }, { name: 1, "address.city": 1, _id: 0 })
db.orders.find({}, { "items.name": 1, total: 1 })          // nested array field
```

### Update
```js
// $set — update specific field
db.users.updateOne({ _id: 6 }, { $set: { premium: true } })

// $set nested field
db.users.updateOne({ _id: 2 }, { $set: { "address.city": "San Francisco" } })

// $inc — increment
db.products.updateOne({ _id: 102 }, { $inc: { stock: -1, reviewCount: 1 } })

// $push — append to array
db.users.updateOne({ _id: 4 }, { $push: { interests: "travel" } })

// $addToSet — append only if not already present
db.users.updateOne({ _id: 4 }, { $addToSet: { interests: "tech" } })

// $pull — remove from array
db.users.updateOne({ _id: 4 }, { $pull: { interests: "sports" } })

// $pop — remove first (-1) or last (1) element
db.users.updateOne({ _id: 1 }, { $pop: { scores: 1 } })

// updateMany — all premium users get a bonus field
db.users.updateMany({ premium: true }, { $set: { loyaltyTier: "gold" } })

// upsert — insert if not found
db.users.updateOne(
    { email: "new@example.com" },
    { $set: { name: "New User", age: 20 } },
    { upsert: true }
)

// replaceOne — full document replacement
db.users.replaceOne({ _id: 8 }, {
    name: "Henry Brown", email: "henry@example.com", age: 46, premium: true,
    address: { city: "Boston", country: "US", zip: "02101" },
    interests: ["cooking"], scores: [65], createdAt: new Date("2022-06-30")
})
```

### Delete
```js
db.users.deleteOne({ email: "test@test.com" })
db.users.deleteMany({ premium: false, age: { $gt: 43 } })
```

---

## 2. Query Operators

### Comparison
```js
$eq   $ne   $gt   $gte   $lt   $lte
db.products.find({ price: { $lt: 50 } })
db.products.find({ stock: { $ne: 0 } })
```

### Logical
```js
// $and (implicit when multiple fields)
db.users.find({ age: { $gte: 30 }, premium: true })

// $or
db.users.find({ $or: [ { premium: true }, { age: { $lt: 27 } } ] })

// $nor — matches none of the conditions
db.users.find({ $nor: [ { premium: true }, { age: { $gt: 40 } } ] })

// $not
db.users.find({ age: { $not: { $gt: 35 } } })
```

### Element
```js
// $exists
db.users.find({ loyaltyTier: { $exists: true } })
db.orders.find({ shippedAt: { $exists: false } })  // pending/cancelled orders

// $type
db.users.find({ age: { $type: "int" } })
```

### Array
```js
// $elemMatch — conditions on same array element
db.orders.find({ items: { $elemMatch: { productId: 103, qty: { $gte: 1 } } } })

// Query on array element by index
db.orders.find({ "items.0.productId": 101 })
```

### Evaluation
```js
// $regex
db.users.find({ name: { $regex: /^A/, $options: "i" } })
db.products.find({ name: { $regex: "pro", $options: "i" } })

// $where (slow, avoid in prod)
db.users.find({ $where: "this.scores.length > 2" })

// $expr — use aggregation expressions in find
db.orders.find({ $expr: { $gt: ["$total", 400] } })

// Match orders where total > price of first item * 1.5
db.orders.find({ $expr: { $gt: ["$total", { $multiply: ["$items.0.price", 1.5] }] } })
```

---

## 3. Aggregation Pipeline

The most powerful feature. Stages are processed in order, each stage receives the output of the previous.

### $match — filter (like WHERE)
```js
db.orders.aggregate([
    { $match: { status: "delivered" } }
])
```

### $project — shape output (like SELECT)
```js
db.orders.aggregate([
    { $project: { userId: 1, total: 1, status: 1, _id: 0 } }
])

// Computed fields
db.orders.aggregate([
    { $project: {
        userId: 1, total: 1,
        itemCount: { $size: "$items" },
        isHighValue: { $gt: ["$total", 300] }
    }}
])
```

### $group — aggregate (like GROUP BY)
```js
// Total revenue and order count per status
db.orders.aggregate([
    { $group: {
        _id: "$status",
        totalRevenue: { $sum: "$total" },
        orderCount: { $count: {} },
        avgOrder: { $avg: "$total" }
    }}
])

// Revenue per user
db.orders.aggregate([
    { $match: { status: { $ne: "cancelled" } } },
    { $group: {
        _id: "$userId",
        totalSpent: { $sum: "$total" },
        orderCount: { $count: {} }
    }},
    { $sort: { totalSpent: -1 } }
])

// $push — collect values into array
db.orders.aggregate([
    { $group: {
        _id: "$userId",
        statuses: { $push: "$status" },
        totals: { $push: "$total" }
    }}
])

// $addToSet — unique values only
db.orders.aggregate([
    { $group: {
        _id: "$userId",
        uniqueStatuses: { $addToSet: "$status" }
    }}
])
```

### $sort, $limit, $skip
```js
db.products.aggregate([
    { $sort: { price: -1 } },
    { $limit: 3 }
])

// Pagination: page 2, 3 per page
db.products.aggregate([
    { $sort: { price: 1 } },
    { $skip: 3 },
    { $limit: 3 }
])
```

### $unwind — flatten array
```js
// Each order item becomes its own document
db.orders.aggregate([
    { $unwind: "$items" }
])

// Revenue per product across all orders
db.orders.aggregate([
    { $match: { status: { $ne: "cancelled" } } },
    { $unwind: "$items" },
    { $group: {
        _id: "$items.productId",
        productName: { $first: "$items.name" },
        totalSold: { $sum: "$items.qty" },
        revenue: { $sum: { $multiply: ["$items.qty", "$items.price"] } }
    }},
    { $sort: { revenue: -1 } }
])
```

### $lookup — join collections
```js
// Orders with user info attached (LEFT JOIN)
db.orders.aggregate([
    { $lookup: {
        from: "users",
        localField: "userId",
        foreignField: "_id",
        as: "user"
    }},
    { $unwind: "$user" },
    { $project: {
        "user.name": 1, "user.email": 1,
        total: 1, status: 1
    }}
])

// Products with all their reviews
db.products.aggregate([
    { $lookup: {
        from: "reviews",
        localField: "_id",
        foreignField: "productId",
        as: "reviews"
    }},
    { $project: {
        name: 1, category: 1, price: 1,
        reviewCount: { $size: "$reviews" },
        avgReviewRating: { $avg: "$reviews.rating" }
    }}
])

// Pipeline lookup (more flexible — like a correlated subquery)
db.users.aggregate([
    { $lookup: {
        from: "orders",
        let: { uid: "$_id" },
        pipeline: [
            { $match: { $expr: { $eq: ["$userId", "$$uid"] } } },
            { $match: { status: "delivered" } },
            { $project: { total: 1, createdAt: 1 } }
        ],
        as: "deliveredOrders"
    }},
    { $addFields: {
        deliveredCount: { $size: "$deliveredOrders" },
        totalSpent: { $sum: "$deliveredOrders.total" }
    }},
    { $sort: { totalSpent: -1 } }
])
```

### $addFields / $set
```js
db.orders.aggregate([
    { $addFields: {
        itemCount: { $size: "$items" },
        isPremiumOrder: { $gt: ["$total", 400] }
    }}
])
```

### $facet — multiple aggregations in one pass
```js
// Great for dashboards
db.products.aggregate([
    { $facet: {
        byCategory: [
            { $group: { _id: "$category", count: { $count: {} }, avgPrice: { $avg: "$price" } } }
        ],
        priceRanges: [
            { $bucket: {
                groupBy: "$price",
                boundaries: [0, 50, 150, 500, 2000],
                default: "Other",
                output: { count: { $count: {} } }
            }}
        ],
        topRated: [
            { $sort: { rating: -1 } },
            { $limit: 3 },
            { $project: { name: 1, rating: 1, _id: 0 } }
        ]
    }}
])
```

### $bucket — range grouping
```js
db.products.aggregate([
    { $bucket: {
        groupBy: "$price",
        boundaries: [0, 50, 100, 200, 500, 2000],
        default: "Expensive",
        output: {
            count: { $count: {} },
            products: { $push: "$name" }
        }
    }}
])
```

### $replaceRoot / $replaceWith
```js
// Promote nested doc to root
db.users.aggregate([
    { $replaceRoot: { newRoot: { $mergeObjects: ["$address", { name: "$name" }] } } }
])
```

### $count
```js
db.orders.aggregate([
    { $match: { status: "delivered" } },
    { $count: "deliveredCount" }
])
```

### Complex multi-stage pipeline
```js
// Top customers: name, total spent, number of unique products ordered
db.orders.aggregate([
    { $match: { status: { $in: ["delivered", "shipped"] } } },
    { $unwind: "$items" },
    { $group: {
        _id: "$userId",
        totalSpent: { $sum: { $multiply: ["$items.qty", "$items.price"] } },
        productsBought: { $addToSet: "$items.productId" }
    }},
    { $lookup: {
        from: "users",
        localField: "_id",
        foreignField: "_id",
        as: "userInfo"
    }},
    { $unwind: "$userInfo" },
    { $project: {
        name: "$userInfo.name",
        premium: "$userInfo.premium",
        totalSpent: 1,
        uniqueProducts: { $size: "$productsBought" }
    }},
    { $sort: { totalSpent: -1 } }
])
```

---

## 4. Indexes

### View indexes
```js
db.users.getIndexes()
db.products.getIndexes()
db.orders.getIndexes()
```

### Single field
```js
db.users.createIndex({ age: 1 })       // ascending
db.users.createIndex({ age: -1 })      // descending
```

### Unique
```js
db.users.createIndex({ email: 1 }, { unique: true })
```

### Compound — order matters: equality → sort → range
```js
db.users.createIndex({ premium: 1, age: -1 })
db.orders.createIndex({ userId: 1, status: 1 })

// This query uses the compound index efficiently:
db.orders.find({ userId: 1, status: "delivered" })
```

### Multikey — on array fields
```js
// Created automatically when indexing an array field
db.users.createIndex({ interests: 1 })
db.users.find({ interests: "tech" })   // uses index
```

### Text — full-text search
```js
// Already created on reviews.title + reviews.body
db.reviews.createIndex({ title: "text", body: "text" })

// Search
db.reviews.find({ $text: { $search: "espresso coffee" } })

// With relevance score
db.reviews.find(
    { $text: { $search: "noise wireless bluetooth" } },
    { score: { $meta: "textScore" } }
).sort({ score: { $meta: "textScore" } })

// Phrase match
db.reviews.find({ $text: { $search: "\"noise cancelling\"" } })

// Exclude word
db.reviews.find({ $text: { $search: "keyboard -gaming" } })
```

### Sparse — only index documents that have the field
```js
db.users.createIndex({ loyaltyTier: 1 }, { sparse: true })
// Documents without loyaltyTier are excluded from index
```

### Partial — index only matching documents
```js
db.orders.createIndex(
    { createdAt: -1 },
    { partialFilterExpression: { status: "delivered" } }
)
// Smaller index, faster for delivered-only queries
```

### TTL — auto-expire documents
```js
// Already created on sessions.expiresAt
db.sessions.createIndex({ expiresAt: 1 }, { expireAfterSeconds: 0 })
// MongoDB checks every ~60s and deletes expired docs
db.sessions.find()  // wait a minute and the expired one disappears
```

### Geospatial — 2dsphere
```js
// Already created on stores.location
db.stores.createIndex({ location: "2dsphere" })
```

### Drop index
```js
db.users.dropIndex({ age: 1 })
db.users.dropIndex("age_1")    // by name
```

---

## 5. Geospatial Queries

Requires 2dsphere index on the field. Coordinates are [longitude, latitude].

### $near — sorted by distance
```js
// Stores near Manhattan (up to 10km)
db.stores.find({
    location: {
        $near: {
            $geometry: { type: "Point", coordinates: [-73.9857, 40.7484] },
            $maxDistance: 10000   // meters
        }
    }
})
```

### $geoWithin — inside a circle
```js
// All stores within 50km of NYC
db.stores.find({
    location: {
        $geoWithin: {
            $centerSphere: [[-73.9857, 40.7484], 50 / 6378.1]  // radius in radians
        }
    }
})
```

### $geoWithin — inside a polygon
```js
// Stores inside a bounding box around New York area
db.stores.find({
    location: {
        $geoWithin: {
            $geometry: {
                type: "Polygon",
                coordinates: [[
                    [-74.3, 40.5],
                    [-73.7, 40.5],
                    [-73.7, 40.9],
                    [-74.3, 40.9],
                    [-74.3, 40.5]
                ]]
            }
        }
    }
})
```

### $geoNear aggregation stage — adds distance field
```js
db.stores.aggregate([
    { $geoNear: {
        near: { type: "Point", coordinates: [-73.9857, 40.7484] },
        distanceField: "distanceMeters",
        maxDistance: 20000,
        spherical: true
    }},
    { $project: { name: 1, city: 1, type: 1, distanceMeters: { $round: ["$distanceMeters", 0] } } }
])
```

---

## 6. Text Search

```js
// Basic search
db.reviews.find({ $text: { $search: "espresso" } })

// Multi-word (OR by default)
db.reviews.find({ $text: { $search: "keyboard gaming mechanical" } })

// Phrase (exact match)
db.reviews.find({ $text: { $search: "\"noise cancelling\"" } })

// Exclude term
db.reviews.find({ $text: { $search: "wireless -bluetooth" } })

// Score + sort by relevance
db.reviews.find(
    { $text: { $search: "laptop performance gaming" } },
    { score: { $meta: "textScore" }, title: 1, _id: 0 }
).sort({ score: { $meta: "textScore" } })

// Combine text search with other filters
db.reviews.find({ $text: { $search: "great" }, rating: 5 })

// In aggregation
db.reviews.aggregate([
    { $match: { $text: { $search: "display screen" } } },
    { $addFields: { score: { $meta: "textScore" } } },
    { $sort: { score: -1 } },
    { $project: { title: 1, score: 1, rating: 1, _id: 0 } }
])
```

---

## 7. explain() — Query Performance

```js
// See if query uses an index
db.users.find({ age: { $gt: 30 } }).explain("queryPlanner")
db.users.find({ age: { $gt: 30 } }).explain("executionStats")

// Key fields in executionStats:
// stage: "IXSCAN" = used index, "COLLSCAN" = full scan (bad on large collections)
// nReturned: docs returned
// totalDocsExamined: docs scanned
// executionTimeMillis: time taken
// indexName: which index was used

// Compound index usage
db.orders.find({ userId: 1, status: "delivered" }).explain("executionStats")

// Covered query — all fields served from index, no doc fetch needed
db.users.find({ age: { $gt: 30 } }, { age: 1, _id: 0 }).explain("executionStats")

// Check which index wins when multiple candidates exist
db.users.find({ premium: true, age: { $gt: 30 } }).explain("executionStats")
```

---

## 8. Schema Validation

Enforce structure on a collection using JSON Schema.

```js
// Create collection with validation
db.createCollection("contacts", {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["name", "email", "phone"],
            properties: {
                name: { bsonType: "string", minLength: 2 },
                email: { bsonType: "string", pattern: "^.+@.+$" },
                phone: { bsonType: "string" },
                age: { bsonType: "int", minimum: 0, maximum: 130 }
            }
        }
    },
    validationAction: "error"   // "warn" to log instead of reject
})

// This fails validation:
db.contacts.insertOne({ name: "X", email: "not-an-email", phone: "123" })

// This passes:
db.contacts.insertOne({ name: "John", email: "john@test.com", phone: "+1-555-0100" })

// Add validation to existing collection
db.runCommand({
    collMod: "users",
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["email", "name"],
            properties: {
                email: { bsonType: "string" },
                age: { bsonType: "int", minimum: 0 }
            }
        }
    },
    validationLevel: "moderate"   // only validate new writes, not existing docs
})

// Check validation rules
db.getCollectionInfos({ name: "contacts" })[0].options.validator
```

---

## 9. Transactions (Multi-document ACID)

Requires a replica set (or mongos for sharded cluster). For local dev, the docker-compose mongo runs standalone — see the note below.

```js
// Note: transactions require replica set. To test locally, start a single-node
// replica set: add --replSet rs0 to mongod, then run rs.initiate()
// See init_replica.md in designShortenService/mongo-data/ for details.

const session = db.getMongo().startSession();
session.startTransaction();
try {
    const users = session.getDatabase("practicedb").users;
    const orders = session.getDatabase("practicedb").orders;

    // Deduct stock
    users.updateOne({ _id: 1 }, { $set: { lastOrderAt: new Date() } });
    orders.insertOne({
        _id: 9999, userId: 1, status: "pending",
        items: [{ productId: 103, name: "Mechanical Keyboard", qty: 1, price: 149.99 }],
        total: 149.99, createdAt: new Date()
    });

    session.commitTransaction();
    print("Transaction committed");
} catch (e) {
    session.abortTransaction();
    print("Transaction aborted:", e);
} finally {
    session.endSession();
}
```

---

## 10. Array Update Operators (Advanced)

```js
// $push with $each + $sort + $slice (keep top 3 scores)
db.users.updateOne(
    { _id: 1 },
    { $push: { scores: { $each: [95, 88], $sort: -1, $slice: 3 } } }
)

// $pull with condition
db.users.updateOne(
    { _id: 2 },
    { $pull: { scores: { $lt: 65 } } }
)

// Update matching array element — positional operator $
db.orders.updateOne(
    { _id: 1001, "items.productId": 101 },
    { $set: { "items.$.price": 1199.99 } }
)

// Update all array elements — $[]
db.orders.updateOne(
    { _id: 1001 },
    { $inc: { "items.$[].price": -10 } }
)

// Update filtered array elements — $[identifier] with arrayFilters
db.orders.updateOne(
    { _id: 1001 },
    { $set: { "items.$[item].discounted": true } },
    { arrayFilters: [ { "item.price": { $gt: 100 } } ] }
)
```

---

## 11. Useful Aggregation Expressions

```js
// Date expressions
db.orders.aggregate([
    { $project: {
        year: { $year: "$createdAt" },
        month: { $month: "$createdAt" },
        dayOfWeek: { $dayOfWeek: "$createdAt" }
    }}
])

// String expressions
db.users.aggregate([
    { $project: {
        nameUpper: { $toUpper: "$name" },
        nameLength: { $strLenCP: "$name" },
        domain: { $arrayElemAt: [ { $split: ["$email", "@"] }, 1 ] }
    }}
])

// Conditional — $cond (ternary)
db.users.aggregate([
    { $project: {
        name: 1, age: 1,
        ageGroup: {
            $cond: {
                if: { $gte: ["$age", 35] },
                then: "senior",
                else: "junior"
            }
        }
    }}
])

// $switch
db.products.aggregate([
    { $project: {
        name: 1, price: 1,
        tier: {
            $switch: {
                branches: [
                    { case: { $lt: ["$price", 50] },  then: "budget" },
                    { case: { $lt: ["$price", 200] }, then: "mid-range" },
                    { case: { $lt: ["$price", 500] }, then: "premium" }
                ],
                default: "luxury"
            }
        }
    }}
])

// $ifNull — default when field is missing
db.orders.aggregate([
    { $project: {
        userId: 1,
        shippedAt: { $ifNull: ["$shippedAt", "Not shipped yet"] }
    }}
])
```

---

## 12. Write Concern & Read Preference (Concepts)

```js
// Write concern — how many replica nodes must acknowledge write
db.users.insertOne(
    { name: "Test" },
    { writeConcern: { w: "majority", j: true, wtimeout: 5000 } }
)
// w: 1 = primary only (default)
// w: "majority" = majority of replica set members
// j: true = write must be journaled to disk

// Read preference — which replica to read from
// primary (default), primaryPreferred, secondary, secondaryPreferred, nearest
// Set in connection string: mongodb://host/?readPreference=secondaryPreferred
```

---

## 13. Change Streams

Real-time notifications when data changes. Requires replica set.

```js
// Watch a collection
const changeStream = db.orders.watch();
changeStream.next()  // blocks until a change happens

// Watch with filter — only inserts
const cs = db.orders.watch([
    { $match: { operationType: { $in: ["insert", "update"] } } }
]);

// In Node.js (event-driven):
// collection.watch().on('change', (change) => console.log(change));
```

---

## 14. Quick Reference — Common Patterns

### Find top N per group
```js
// Top 2 most expensive products per category
db.products.aggregate([
    { $sort: { category: 1, price: -1 } },
    { $group: {
        _id: "$category",
        products: { $push: { name: "$name", price: "$price" } }
    }},
    { $project: { top2: { $slice: ["$products", 2] } } }
])
```

### Distinct values
```js
db.users.distinct("address.country")
db.products.distinct("category")
db.orders.distinct("status")
```

### Count documents
```js
db.users.countDocuments({ premium: true })
db.orders.countDocuments({ status: "delivered" })
db.orders.estimatedDocumentCount()   // faster, uses metadata
```

### findAndModify / findOneAndUpdate
```js
// Atomic find + update, returns the document
db.orders.findOneAndUpdate(
    { _id: 1010, status: "pending" },
    { $set: { status: "shipped", shippedAt: new Date() } },
    { returnDocument: "after" }   // return updated doc
)
```

### Bulk operations
```js
db.products.bulkWrite([
    { updateOne: { filter: { _id: 101 }, update: { $inc: { stock: -1 } } } },
    { updateOne: { filter: { _id: 102 }, update: { $inc: { stock: -2 } } } },
    { insertOne: { document: { name: "New Product", category: "Electronics", price: 9.99 } } }
])
```

### Aggregation to new collection
```js
// $out — replace collection
db.orders.aggregate([
    { $match: { status: "delivered" } },
    { $group: { _id: "$userId", totalSpent: { $sum: "$total" } } },
    { $out: "user_spending_summary" }
])

// $merge — upsert into existing collection
db.orders.aggregate([
    { $group: { _id: "$userId", orderCount: { $count: {} } } },
    { $merge: { into: "user_stats", on: "_id", whenMatched: "merge", whenNotMatched: "insert" } }
])
```

---

## 15. mongosh Helpers

```js
// Database info
show dbs
use practicedb
show collections
db.stats()
db.users.stats()

// Collection info
db.getCollectionInfos()

// Profile slow queries (>100ms)
db.setProfilingLevel(1, { slowms: 100 })
db.system.profile.find().sort({ ts: -1 }).limit(5)
db.setProfilingLevel(0)   // turn off

// Current operations
db.currentOp()

// Kill a long-running op
db.killOp(<opid>)
```
