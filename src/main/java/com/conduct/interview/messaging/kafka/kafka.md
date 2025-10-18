cd /opt/bitnami/kafka/bin

/opt/bitnami/kafka/bin/kafka-topics.sh --bootstrap-server kafka:29092 --list


/opt/bitnami/kafka/bin/kafka-topics.sh \
--bootstrap-server kafka:29092 \
--create \
--topic my-new-topic \
--partitions 3 \
--replication-factor 1

✅ Partitions → divide work across consumers

✅ Replication → keeps copies for durability

✅ Group ID → decides if consumers share work or read independently