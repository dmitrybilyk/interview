# --- AnotherBoot1: Create a new order ---
curl -X POST http://localhost:8091/orders \
-H "Content-Type: application/json" \
-d '{"customerName": "Alice", "total": 120.5}'

# --- AnotherBoot1: View all orders in MongoDB ---
curl http://localhost:8091/orders

docker exec -it my-mongo mongosh -u user123 -p pass123 --authenticationDatabase admin



# --- AnotherBoot2: Test payment service directly ---
curl -X POST http://localhost:8092/pay \
-H "Content-Type: application/json" \
-d '{"id": "123", "customerName": "Bob", "total": 50.0}'

# --- AnotherBoot3: (Consumer) no curl endpoints, but see logs for Kafka messages ---

# --- MongoDB check ---
# Run in terminal to see data stored in Mongo
mongosh
use ordersdb
db.order.find().pretty()

# --- Kafka check ---
# List topics
docker exec -it kafka kafka-topics.sh --bootstrap-server localhost:9092 --list

# Consume messages from "orders" topic
docker exec -it kafka kafka-console-consumer.sh \
--bootstrap-server localhost:9092 \
--topic orders \
--from-beginning
