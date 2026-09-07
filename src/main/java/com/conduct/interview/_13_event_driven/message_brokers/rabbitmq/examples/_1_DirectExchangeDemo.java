package com.conduct.interview._13_event_driven.message_brokers.rabbitmq.examples;

import com.rabbitmq.client.*;

/**
 * DIRECT exchange: exact routing-key match.
 *
 * Producer sends to "demo-direct" with routing key "order".
 * Only queues bound with routing key "order" receive it.
 *
 * Run: _0_InfraSetup → this class (producer + consumer in threads).
 *
 * Difference from Topic: Direct uses exact string match, no wildcards.
 * Use for: task queues, targeted routing (process order, send email, etc.)
 */
public class _1_DirectExchangeDemo {

    public static void main(String[] args) throws Exception {
        // Consumer thread
        Thread consumer = new Thread(() -> {
            try (Connection conn = _0_InfraSetup.factory().newConnection();
                 Channel ch = conn.createChannel()) {

                ch.basicQos(1);  // fair dispatch: one message at a time
                System.out.println("[DIRECT consumer] Waiting for orders...");

                ch.basicConsume(_0_InfraSetup.ORDERS_Q, false, (tag, msg) -> {
                    String body = new String(msg.getBody());
                    System.out.println("[DIRECT consumer] Received: " + body);
                    ch.basicAck(msg.getEnvelope().getDeliveryTag(), false);
                }, tag -> {});

                Thread.sleep(10_000);
            } catch (Exception e) { e.printStackTrace(); }
        });
        consumer.setDaemon(true);
        consumer.start();

        // Producer
        try (Connection conn = _0_InfraSetup.factory().newConnection();
             Channel ch = conn.createChannel()) {

            for (int i = 1; i <= 5; i++) {
                String msg = "{\"orderId\":" + i + ",\"amount\":" + (i * 50) + "}";
                ch.basicPublish(_0_InfraSetup.DIRECT_EX, "order",
                        MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
                System.out.println("[DIRECT producer] Sent: " + msg);
                Thread.sleep(300);
            }
        }

        consumer.join();
    }
}
