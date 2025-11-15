print("Starting fresh insert with ISODate()...");

// Drop
db.sales.drop();
db.products.drop();
db.customers.drop();

// Products
db.products.insertMany([
    { _id: 1, name: "Laptop", category: "Electronics", price: 1200 },
    { _id: 2, name: "Mouse", category: "Electronics", price: 25 },
    { _id: 3, name: "Coffee", category: "Food", price: 5 },
    { _id: 4, name: "T-Shirt", category: "Clothing", price: 20 },
    { _id: 6, name: "Headphones", category: "Electronics", price: 80 },
    { _id: 7, name: "Jeans", category: "Clothing", price: 60 },
    { _id: 8, name: "Tea", category: "Food", price: 4 },
    { _id: 9, name: "Monitor", category: "Electronics", price: 300 }
]);

// Customers
db.customers.insertMany([
    { _id: 1, name: "Alice", region: "North" },
    { _id: 2, name: "Bob", region: "South" },
    { _id: 3, name: "Charlie", region: "North" },
    { _id: 4, name: "Diana", region: "West" }
]);

// Sales — 100% ISODate
db.sales.insertMany([
    {
        customerId: 1,
        date: ISODate("2025-11-01T00:00:00.000Z"),
        items: [ { productId: 1, qty: 1, price: 1200 }, { productId: 2, qty: 2, price: 25 } ]
    },
    {
        customerId: 2,
        date: ISODate("2025-11-02T00:00:00.000Z"),
        items: [ { productId: 3, qty: 5, price: 5 }, { productId: 8, qty: 3, price: 4 } ]
    },
    {
        customerId: 1,
        date: ISODate("2025-11-03T00:00:00.000Z"),
        items: [ { productId: 6, qty: 1, price: 80 } ]
    },
    {
        customerId: 3,
        date: ISODate("2025-11-04T00:00:00.000Z"),
        items: [ { productId: 4, qty: 2, price: 20 }, { productId: 7, qty: 1, price: 60 } ]
    },
    {
        customerId: 4,
        date: ISODate("2025-11-05T00:00:00.000Z"),
        items: [ { productId: 9, qty: 1, price: 300 }, { productId: 2, qty: 1, price: 25 } ]
    }
]);

// VERIFY TYPE
const doc = db.sales.findOne();
print("\nVERIFY: date type =", doc.date);
print("Is Date?", doc.date instanceof Date);
print("ISOString:", doc.date.toISOString());

print("\nAll done. 5 sales inserted with REAL Date type.");