Services

order-service

Receives REST requests to create orders

Stores orders in MongoDB

Calls payment-service via REST (synchronous)

Publishes OrderCreated event to Kafka (as Order object)
