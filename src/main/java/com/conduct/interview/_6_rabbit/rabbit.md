# AMQP and RabbitMQ Overview

**AMQP** - Advanced Message Queuing Protocol. It standardizes messaging behavior
between producers, consumers, brokers, ensuring reliable communication across
distributed systems.

**RabbitMQ** - Open-source message broker which implements the AMQP protocol and
facilitates communication between systems acting as a middleman. Sender and
receiver applications don't know about each other, which leads to having loosely
coupled systems that are easy to scale and maintain.

RabbitMQ supports **AMQP**, **STOMP**, **HTTP** protocols, etc.

## Key Concepts

- **Producer** - An application that sends messages to the message broker
  (RabbitMQ).
- **Consumer** - An application that receives messages from the message broker.
- **Message** - The payload which is sent from the Producer to the Consumer.
- **Queue** - A buffer that stores the messages sent by the Producer until they
  are processed (consumed) by the Consumer.
- **Exchange** - Receives messages from the producer and routes them to queues.

## Types of Exchange

- **Direct Exchange** - Routes messages based on matching of message's routing
  key and queue's binding key.
- **Fanout Exchange** - Broadcasts messages to all bound queues, ignoring
  routing keys.
- **Topic Exchange** - Routes messages based on matching pattern between
  messages' routing keys and queues' binding keys.
- **Headers Exchange** - Routes messages based on headers rather than the
  routing key.

## Other Concepts

- **Binding** - Connection between an exchange and a queue.
- **Routing Key** - String used by exchanges to route messages to queues.

## Core Features of RabbitMQ

- **Message Durability** - Ensures messages are not lost.
- **Acknowledgment** - Confirms successful message processing.
- **Dead-letter Exchange** - Handles unroutable or expired messages.
- **Prefetch Count** - Controls the number of messages a consumer can process.
- **Priority Queues** - Allows prioritization of messages.
