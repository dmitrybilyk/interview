package com.conduct.interview._13_event_driven.message_brokers.rabbitmq.examples;

import com.rabbitmq.client.*;

/**
 * TOPIC exchange: wildcard routing-key matching.
 *
 * Binding pattern: "logs.*"
 *   *  = exactly one word
 *   #  = zero or more words
 *
 * So "logs.info", "logs.error", "logs.warn" all match "logs.*".
 * But "logs.app.info" does NOT match "logs.*" — it matches "logs.#".
 *
 * Run: _0_InfraSetup → this class.
 *
 * Use for: routing events by category/severity/region (e.g. "eu.order.created").
 */
public class _2_TopicExchangeDemo {

    public static void main(String[] args) throws Exception {
        Thread consumer = new Thread(() -> {
            try (Connection conn = _0_InfraSetup.factory().newConnection();
                 Channel ch = conn.createChannel()) {

                ch.basicQos(1);
                System.out.println("[TOPIC consumer] Watching logs.* ...");

                ch.basicConsume(_0_InfraSetup.LOGS_Q, false, (tag, msg) -> {
                    System.out.printf("[TOPIC consumer] key=%-15s body=%s%n",
                            msg.getEnvelope().getRoutingKey(), new String(msg.getBody()));
                    ch.basicAck(msg.getEnvelope().getDeliveryTag(), false);
                }, t -> {});

                Thread.sleep(10_000);
            } catch (Exception e) { e.printStackTrace(); }
        });
        consumer.setDaemon(true);
        consumer.start();

        try (Connection conn = _0_InfraSetup.factory().newConnection();
             Channel ch = conn.createChannel()) {

            String[] keys = {"logs.info", "logs.error", "logs.warn", "metrics.cpu", "logs.debug"};
            for (String key : keys) {
                String body = "Event from routing key: " + key;
                ch.basicPublish(_0_InfraSetup.TOPIC_EX, key,
                        MessageProperties.PERSISTENT_TEXT_PLAIN, body.getBytes());
                System.out.printf("[TOPIC producer] Sent key=%-15s (matches logs.*: %s)%n",
                        key, key.startsWith("logs.") && key.split("\\.").length == 2);
                Thread.sleep(200);
            }
        }

        consumer.join();
    }
}
