// Runs automatically on first container start via docker-entrypoint-initdb.d
// Re-run manually: mongosh "mongodb://admin:admin123@localhost:27017" --file init/01-init.js

db = db.getSiblingDB('practicedb');

// ── DROP ALL ───────────────────────────────────────────────────────────────
['users','products','orders','reviews','stores','sessions'].forEach(c => db[c].drop());

// ── USERS ──────────────────────────────────────────────────────────────────
db.users.insertMany([
    {
        _id: 1, name: "Alice Johnson", email: "alice@example.com", age: 29, premium: true,
        address: { city: "New York", country: "US", zip: "10001" },
        interests: ["tech", "gaming", "music"],
        scores: [85, 90, 78],
        createdAt: new Date("2023-01-15")
    },
    {
        _id: 2, name: "Bob Smith", email: "bob@example.com", age: 35, premium: false,
        address: { city: "Los Angeles", country: "US", zip: "90001" },
        interests: ["sports", "cooking"],
        scores: [60, 72, 65],
        createdAt: new Date("2023-03-20")
    },
    {
        _id: 3, name: "Clara Müller", email: "clara@example.com", age: 26, premium: true,
        address: { city: "Berlin", country: "DE", zip: "10115" },
        interests: ["music", "travel", "tech"],
        scores: [92, 88, 95],
        createdAt: new Date("2023-05-10")
    },
    {
        _id: 4, name: "David Park", email: "david@example.com", age: 42, premium: false,
        address: { city: "Seoul", country: "KR", zip: "04524" },
        interests: ["gaming", "cooking", "sports"],
        scores: [70, 68, 74],
        createdAt: new Date("2022-11-01")
    },
    {
        _id: 5, name: "Eva García", email: "eva@example.com", age: 31, premium: true,
        address: { city: "Madrid", country: "ES", zip: "28001" },
        interests: ["travel", "music", "cooking"],
        scores: [88, 91, 83],
        createdAt: new Date("2023-07-22")
    },
    {
        _id: 6, name: "Frank Chen", email: "frank@example.com", age: 24, premium: false,
        address: { city: "Shanghai", country: "CN", zip: "200000" },
        interests: ["tech", "gaming"],
        scores: [55, 60, 58],
        createdAt: new Date("2024-01-05")
    },
    {
        _id: 7, name: "Grace Lee", email: "grace@example.com", age: 38, premium: true,
        address: { city: "London", country: "GB", zip: "EC1A" },
        interests: ["travel", "sports", "tech"],
        scores: [79, 82, 77],
        createdAt: new Date("2023-09-14")
    },
    {
        _id: 8, name: "Henry Brown", email: "henry@example.com", age: 45, premium: false,
        address: { city: "New York", country: "US", zip: "10002" },
        interests: ["cooking", "sports"],
        scores: [50, 62, 55],
        createdAt: new Date("2022-06-30")
    }
]);

// ── PRODUCTS ───────────────────────────────────────────────────────────────
db.products.insertMany([
    {
        _id: 101, name: "Laptop Pro 15", category: "Electronics", brand: "TechCo",
        price: 1299.99, stock: 45,
        tags: ["laptop", "portable", "pro"],
        specs: { ram: 16, storage: 512, screen: 15.6 },
        rating: 4.5, reviewCount: 120
    },
    {
        _id: 102, name: "Wireless Mouse", category: "Electronics", brand: "TechCo",
        price: 29.99, stock: 300,
        tags: ["mouse", "wireless", "ergonomic"],
        specs: { dpi: 1600, buttons: 6 },
        rating: 4.2, reviewCount: 85
    },
    {
        _id: 103, name: "Mechanical Keyboard", category: "Electronics", brand: "KeyMaster",
        price: 149.99, stock: 120,
        tags: ["keyboard", "mechanical", "gaming"],
        specs: { switches: "Cherry MX Red", layout: "TKL", backlit: true },
        rating: 4.7, reviewCount: 200
    },
    {
        _id: 104, name: "Running Shoes X1", category: "Sports", brand: "SpeedRun",
        price: 89.99, stock: 80,
        tags: ["shoes", "running", "lightweight"],
        specs: { material: "mesh", sole: "rubber" },
        rating: 4.3, reviewCount: 65
    },
    {
        _id: 105, name: "Yoga Mat Pro", category: "Sports", brand: "ZenFit",
        price: 39.99, stock: 150,
        tags: ["yoga", "mat", "non-slip"],
        specs: { thickness: 6, material: "TPE" },
        rating: 4.6, reviewCount: 95
    },
    {
        _id: 106, name: "Espresso Machine", category: "Kitchen", brand: "CaféPro",
        price: 349.99, stock: 30,
        tags: ["coffee", "espresso", "kitchen"],
        specs: { pressure: 15, capacity: 1.5, grinder: true },
        rating: 4.8, reviewCount: 310
    },
    {
        _id: 107, name: "Blender Ultra", category: "Kitchen", brand: "BlendKing",
        price: 79.99, stock: 60,
        tags: ["blender", "kitchen", "smoothie"],
        specs: { power: 1200, speed: 10 },
        rating: 4.1, reviewCount: 42
    },
    {
        _id: 108, name: "4K Monitor 27\"", category: "Electronics", brand: "VistaMax",
        price: 499.99, stock: 25,
        tags: ["monitor", "4k", "display"],
        specs: { resolution: "3840x2160", refresh: 144, panel: "IPS" },
        rating: 4.9, reviewCount: 175
    },
    {
        _id: 109, name: "Travel Backpack 40L", category: "Travel", brand: "WanderGear",
        price: 69.99, stock: 90,
        tags: ["backpack", "travel", "waterproof"],
        specs: { capacity: 40, material: "nylon", waterproof: true },
        rating: 4.4, reviewCount: 130
    },
    {
        _id: 110, name: "Noise Cancelling Headphones", category: "Electronics", brand: "SoundWave",
        price: 249.99, stock: 55,
        tags: ["headphones", "wireless", "anc"],
        specs: { driver: 40, battery: 30, anc: true },
        rating: 4.6, reviewCount: 290
    },
    {
        _id: 111, name: "Chef's Knife Set", category: "Kitchen", brand: "SharpEdge",
        price: 119.99, stock: 40,
        tags: ["knife", "kitchen", "professional"],
        specs: { pieces: 5, material: "stainless steel", handle: "wood" },
        rating: 4.7, reviewCount: 88
    },
    {
        _id: 112, name: "Camping Tent 4-Person", category: "Travel", brand: "WanderGear",
        price: 189.99, stock: 20,
        tags: ["tent", "camping", "waterproof"],
        specs: { capacity: 4, season: 3, weight: 3.2 },
        rating: 4.3, reviewCount: 55
    }
]);

// ── ORDERS ─────────────────────────────────────────────────────────────────
db.orders.insertMany([
    {
        _id: 1001, userId: 1, status: "delivered",
        items: [
            { productId: 101, name: "Laptop Pro 15", qty: 1, price: 1299.99 },
            { productId: 102, name: "Wireless Mouse", qty: 1, price: 29.99 }
        ],
        total: 1329.98,
        createdAt: new Date("2024-01-10"), shippedAt: new Date("2024-01-12")
    },
    {
        _id: 1002, userId: 2, status: "shipped",
        items: [
            { productId: 104, name: "Running Shoes X1", qty: 2, price: 89.99 }
        ],
        total: 179.98,
        createdAt: new Date("2024-02-05"), shippedAt: new Date("2024-02-07")
    },
    {
        _id: 1003, userId: 3, status: "delivered",
        items: [
            { productId: 103, name: "Mechanical Keyboard", qty: 1, price: 149.99 },
            { productId: 110, name: "Noise Cancelling Headphones", qty: 1, price: 249.99 }
        ],
        total: 399.98,
        createdAt: new Date("2024-01-20"), shippedAt: new Date("2024-01-22")
    },
    {
        _id: 1004, userId: 1, status: "delivered",
        items: [
            { productId: 108, name: "4K Monitor 27\"", qty: 1, price: 499.99 }
        ],
        total: 499.99,
        createdAt: new Date("2024-03-01"), shippedAt: new Date("2024-03-03")
    },
    {
        _id: 1005, userId: 5, status: "pending",
        items: [
            { productId: 106, name: "Espresso Machine", qty: 1, price: 349.99 },
            { productId: 111, name: "Chef's Knife Set", qty: 1, price: 119.99 }
        ],
        total: 469.98,
        createdAt: new Date("2024-04-18")
    },
    {
        _id: 1006, userId: 4, status: "cancelled",
        items: [
            { productId: 105, name: "Yoga Mat Pro", qty: 3, price: 39.99 }
        ],
        total: 119.97,
        createdAt: new Date("2024-02-14")
    },
    {
        _id: 1007, userId: 6, status: "delivered",
        items: [
            { productId: 102, name: "Wireless Mouse", qty: 1, price: 29.99 },
            { productId: 103, name: "Mechanical Keyboard", qty: 1, price: 149.99 }
        ],
        total: 179.98,
        createdAt: new Date("2024-03-15"), shippedAt: new Date("2024-03-17")
    },
    {
        _id: 1008, userId: 7, status: "delivered",
        items: [
            { productId: 109, name: "Travel Backpack 40L", qty: 1, price: 69.99 },
            { productId: 112, name: "Camping Tent 4-Person", qty: 1, price: 189.99 }
        ],
        total: 259.98,
        createdAt: new Date("2024-04-05"), shippedAt: new Date("2024-04-07")
    },
    {
        _id: 1009, userId: 3, status: "shipped",
        items: [
            { productId: 108, name: "4K Monitor 27\"", qty: 1, price: 499.99 }
        ],
        total: 499.99,
        createdAt: new Date("2024-04-20"), shippedAt: new Date("2024-04-22")
    },
    {
        _id: 1010, userId: 8, status: "pending",
        items: [
            { productId: 107, name: "Blender Ultra", qty: 1, price: 79.99 }
        ],
        total: 79.99,
        createdAt: new Date("2024-05-01")
    },
    {
        _id: 1011, userId: 5, status: "delivered",
        items: [
            { productId: 105, name: "Yoga Mat Pro", qty: 1, price: 39.99 },
            { productId: 104, name: "Running Shoes X1", qty: 1, price: 89.99 }
        ],
        total: 129.98,
        createdAt: new Date("2024-01-30"), shippedAt: new Date("2024-02-01")
    },
    {
        _id: 1012, userId: 2, status: "delivered",
        items: [
            { productId: 110, name: "Noise Cancelling Headphones", qty: 1, price: 249.99 }
        ],
        total: 249.99,
        createdAt: new Date("2024-03-10"), shippedAt: new Date("2024-03-12")
    }
]);

// ── REVIEWS ────────────────────────────────────────────────────────────────
db.reviews.insertMany([
    {
        _id: 1, productId: 101, userId: 1, rating: 5,
        title: "Best laptop I've ever owned",
        body: "Incredible performance for development and gaming. Battery lasts all day.",
        createdAt: new Date("2024-01-25"), helpful: 42
    },
    {
        _id: 2, productId: 101, userId: 3, rating: 4,
        title: "Solid machine but runs hot",
        body: "Great display and fast processor. Thermal throttling under heavy load is the only issue.",
        createdAt: new Date("2024-02-10"), helpful: 18
    },
    {
        _id: 3, productId: 103, userId: 6, rating: 5,
        title: "Perfect gaming keyboard",
        body: "The Cherry MX Red switches are smooth and responsive. Backlight looks amazing in the dark.",
        createdAt: new Date("2024-03-20"), helpful: 35
    },
    {
        _id: 4, productId: 106, userId: 5, rating: 5,
        title: "Makes barista-quality espresso at home",
        body: "Pressure is perfect, heats up fast. The built-in grinder is a game changer for fresh coffee.",
        createdAt: new Date("2024-02-28"), helpful: 60
    },
    {
        _id: 5, productId: 108, userId: 1, rating: 5,
        title: "Stunning 4K display, zero compromises",
        body: "Colors are vivid, 144Hz gaming is butter smooth. IPS panel has excellent viewing angles.",
        createdAt: new Date("2024-03-15"), helpful: 55
    },
    {
        _id: 6, productId: 110, userId: 3, rating: 4,
        title: "Great noise cancelling but connection drops occasionally",
        body: "ANC is top-notch, audio quality is rich. Bluetooth drops once in a while on busy networks.",
        createdAt: new Date("2024-02-05"), helpful: 28
    },
    {
        _id: 7, productId: 104, userId: 2, rating: 4,
        title: "Lightweight and comfortable running shoes",
        body: "Mesh upper breathes well. Good cushioning for long runs but not ideal for trails.",
        createdAt: new Date("2024-02-20"), helpful: 14
    },
    {
        _id: 8, productId: 105, userId: 5, rating: 5,
        title: "Best yoga mat I have tried",
        body: "Non-slip surface is excellent even when sweaty. Thick enough for hard floors.",
        createdAt: new Date("2024-02-15"), helpful: 22
    },
    {
        _id: 9, productId: 109, userId: 7, rating: 4,
        title: "Spacious and well-organized travel backpack",
        body: "Fits everything for a weekend trip. Laptop compartment is padded. Waterproofing works well.",
        createdAt: new Date("2024-04-10"), helpful: 30
    },
    {
        _id: 10, productId: 112, userId: 7, rating: 4,
        title: "Easy setup, good for 3-season camping",
        body: "Poles are sturdy and tent stakes well in soft ground. Not suitable for heavy snow.",
        createdAt: new Date("2024-04-12"), helpful: 19
    },
    {
        _id: 11, productId: 107, userId: 8, rating: 3,
        title: "Powerful but loud blender",
        body: "Smoothies come out perfectly smooth. Motor noise is significant at full speed.",
        createdAt: new Date("2024-05-05"), helpful: 8
    },
    {
        _id: 12, productId: 111, userId: 5, rating: 5,
        title: "Professional quality knife set",
        body: "Stainless steel holds edge very well. Wood handles feel premium. Worth every penny.",
        createdAt: new Date("2024-01-28"), helpful: 47
    }
]);

// ── STORES (GeoJSON for geospatial queries) ────────────────────────────────
db.stores.insertMany([
    {
        _id: 1, name: "NYC Flagship Store",
        type: "store", city: "New York",
        location: { type: "Point", coordinates: [-73.9857, 40.7484] }  // Manhattan
    },
    {
        _id: 2, name: "Brooklyn Outlet",
        type: "store", city: "New York",
        location: { type: "Point", coordinates: [-73.9442, 40.6782] }  // Brooklyn
    },
    {
        _id: 3, name: "LA Showroom",
        type: "store", city: "Los Angeles",
        location: { type: "Point", coordinates: [-118.2437, 34.0522] }
    },
    {
        _id: 4, name: "East Coast Warehouse",
        type: "warehouse", city: "Newark",
        location: { type: "Point", coordinates: [-74.1724, 40.7357] }
    },
    {
        _id: 5, name: "Chicago Distribution",
        type: "warehouse", city: "Chicago",
        location: { type: "Point", coordinates: [-87.6298, 41.8781] }
    }
]);

// ── SESSIONS (TTL index demo — expiresAt) ──────────────────────────────────
db.sessions.insertMany([
    {
        userId: 1, token: "tok_abc123",
        expiresAt: new Date(Date.now() + 1000 * 60 * 60 * 2),  // 2h from now
        createdAt: new Date()
    },
    {
        userId: 3, token: "tok_def456",
        expiresAt: new Date(Date.now() + 1000 * 60 * 30),       // 30min from now
        createdAt: new Date()
    },
    {
        userId: 5, token: "tok_ghi789",
        expiresAt: new Date(Date.now() - 1000 * 60 * 5),        // already expired (5min ago)
        createdAt: new Date()
    }
]);

// ── INDEXES ────────────────────────────────────────────────────────────────
// Single field
db.users.createIndex({ email: 1 }, { unique: true });
db.users.createIndex({ age: 1 });
db.users.createIndex({ "address.country": 1 });

// Compound
db.users.createIndex({ premium: 1, age: -1 });

// Multikey (on array field)
db.users.createIndex({ interests: 1 });

// Text search
db.reviews.createIndex({ title: "text", body: "text" });

// Compound on orders for common query patterns
db.orders.createIndex({ userId: 1, status: 1 });
db.orders.createIndex({ createdAt: -1 });
db.orders.createIndex({ "items.productId": 1 });

// Products compound + text
db.products.createIndex({ category: 1, price: 1 });
db.products.createIndex({ tags: 1 });
db.products.createIndex({ name: "text", tags: "text" });

// Geospatial (2dsphere)
db.stores.createIndex({ location: "2dsphere" });

// TTL — auto-delete expired sessions (MongoDB checks every ~60s)
db.sessions.createIndex({ expiresAt: 1 }, { expireAfterSeconds: 0 });

print("✓ practicedb seeded: users(8) products(12) orders(12) reviews(12) stores(5) sessions(3)");
print("✓ Indexes created: unique, compound, multikey, text, 2dsphere, TTL");
