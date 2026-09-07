package com.conduct.interview._13_event_driven.message_brokers.rabbitmq.examples;

import com.rabbitmq.client.*;

/**
 * FANOUT exchange: broadcast to ALL bound queues, ignoring routing key.
 *
 * One producer message → fanout-q-1 AND fanout-q-2 both receive it.
 * This is the Pub/Sub (publish-subscribe) pattern.
 *
 * Run: _0_InfraSetup → this class.
 *
 * Use for: cache invalidation, config updates, event notifications to multiple services.
 */
public class _3_FanoutExchangeDemo {

    public static void main(String[] args) throws Exception {
        // Two independent consumers
        for (String qName : new String[]{_0_InfraSetup.FANOUT_Q1, _0_InfraSetup.FANOUT_Q2}) {
            Thread t = new Thread(() -> {
                try (Connection conn = _0_InfraSetup.factory().newConnection();
                     Channel ch = conn.createChannel()) {

                    ch.basicQos(1);
                    System.out.println("[FANOUT consumer:" + qName + "] Waiting...");

                    ch.basicConsume(qName, false, (tag, msg) -> {
                        System.out.printf("[FANOUT consumer:%s] Received: %s%n",
                                qName, new String(msg.getBody()));
                        ch.basicAck(msg.getEnvelope().getDeliveryTag(), false);
                    }, tag -> {});

                    Thread.sleep(10_000);
                } catch (Exception e) { e.printStackTrace(); }
            });
            t.setDaemon(true);
            t.start();
        }

        Thread.sleep(500);  // let consumers attach

        try (Connection conn = _0_InfraSetup.factory().newConnection();
             Channel ch = conn.createChannel()) {

            for (int i = 1; i <= 3; i++) {
                String event = "{\"event\":\"CONFIG_UPDATED\",\"version\":" + i + "}";
                // Routing key is ignored by fanout
                ch.basicPublish(_0_InfraSetup.FANOUT_EX, "",
                        MessageProperties.PERSISTENT_TEXT_PLAIN, event.getBytes());
                System.out.println("[FANOUT producer] Broadcast: " + event);
                Thread.sleep(400);
            }
        }

        Thread.sleep(5_000);
    }
}
