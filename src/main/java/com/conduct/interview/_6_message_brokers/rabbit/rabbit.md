RabbitMQ Interview Guide
1. Core Architecture
   RabbitMQ is a Smart Broker. It manages message lifecycle and routing logic internally.

Producer: Sends messages to an Exchange.

Exchange: The routing engine (decides where data goes).

Binding: The link/rule between Exchange and Queue.

Queue: The buffer where messages sit.

Consumer: Grabs messages from the Queue.

2. Exchange Types (Routing)
   Direct: Exact match of Routing Key.

Use: Targeted tasks (e.g., logs.error).

Topic: Pattern match using wildcards.

*: Exactly one word.

#: Zero or more words.

Use: Geographic or categorized routing.

Fanout: Ignores keys; broadcasts to ALL bound queues.

Use: Pub/Sub, configuration updates.

Headers: Uses message header attributes instead of keys.

Use: Complex metadata routing.

3. Delivery Guarantees
   basicAck: Consumer says "I'm done, delete it."

basicNack: Consumer says "I failed."

requeue=true: Back to the queue.

requeue=false: Discard or send to DLX.

Prefetch (QoS): Controls how many unacked messages a worker can hold. basicQos(1) ensures "Fair Dispatch."

4. Reliability Features
   Durability: Queues/Exchanges survive a broker restart.

Persistence: Messages are written to disk.

DLX (Dead Letter Exchange):

Automatic "Trash Bin."

Handles: Rejected messages, Expired (TTL), or Queue length limit hit.

TTL: Time-To-Live. Messages die if not consumed in X ms.