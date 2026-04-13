Apache Kafka Overview
Apache Kafka is a distributed event streaming platform. Unlike traditional message brokers, 
Kafka is architected as a distributed, replicated commit log. It is designed to handle high-volume, real-time data feeds with extreme throughput and low latency.

Instead of "pushing" messages to consumers and deleting them, Kafka "appends" messages to a log 
and lets consumers "pull" data at their own pace. This makes it ideal for Event-Driven Architectures 
and big data processing.

Key Concepts
Producer - An application that publishes (sends) events to Kafka topics.

Consumer - An application that subscribes to topics and processes the events.

Event (Record) - The unit of data in Kafka. It consists of a key, value, timestamp, and optional 
metadata headers.

Topic - A category or feed name to which records are stored. Similar to a table in a database, 
but without constraints.

Partition - Topics are divided into partitions for scalability. Each partition is an ordered, 
immutable sequence of records.

Broker - A Kafka server. A group of brokers forms a Cluster, which shares the load and provides redundancy.

Storage & Flow Concepts
Offset - A unique sequential ID assigned to every message within a partition. Consumers use offsets 
to track their position in the stream.

Retention Policy - Defines how long Kafka keeps data (based on time or size). Data is not deleted 
immediately after consumption.

Replication - Records are copied across multiple brokers. If one broker fails, another takes over 
(High Availability).

Zookeeper / KRaft - Control plane services that manage cluster metadata, broker registration, 
and leader election.

Consumer Groups
Consumer Group - A group of consumers working together to consume data from a topic. Kafka ensures 
each partition is read by only one member of the group.

Rebalancing - The process of reassigning partitions when a consumer joins or leaves a group.

Parallelism - The number of partitions determines the maximum number of parallel consumers in a single group.

Core Features of Kafka
Persistence - Messages are written to disk and replicated, ensuring zero data loss.

High Throughput - Capable of processing millions of messages per second using sequential I/O and Zero-copy.

Scalability - Scales horizontally by adding more brokers and increasing the number of partitions.

Replayability - Because data is persistent, consumers can "rewind" to a previous offset and re-process old data.

Decoupling - Complete isolation between producers and consumers; the producer does not need 
to know if the consumer is online.