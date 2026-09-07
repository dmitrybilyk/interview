package com.conduct.interview._13_event_driven.message_brokers.kafka.examples;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Run FIRST. Creates all topics used by the other examples.
 *
 * topics:
 *   orders-topic      – 3 partitions, used by keyed-producer examples
 *   orders-dlq        – dead-letter destination for failed orders
 *   tx-topic          – exactly-once demo
 */
public class _0_TopicSetup {

    static final String BOOTSTRAP = "localhost:9092";
    static final String ORDERS    = "orders-topic";
    static final String DLQ       = "orders-dlq";
    static final String TX_TOPIC  = "tx-topic";

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP);

        try (AdminClient admin = AdminClient.create(props)) {
            List<NewTopic> topics = List.of(
                    new NewTopic(ORDERS,   3, (short) 1),
                    new NewTopic(DLQ,      1, (short) 1),
                    new NewTopic(TX_TOPIC, 3, (short) 1)
            );
            admin.createTopics(topics).all().get();
            System.out.println("Topics created: " + topics.stream().map(NewTopic::name).toList());
        } catch (Exception e) {
            System.out.println("Some topics may already exist: " + e.getMessage());
        }
    }
}
