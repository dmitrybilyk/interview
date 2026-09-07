package com.conduct.interview._13_event_driven.message_brokers.rabbitmq.examples;

import com.rabbitmq.client.*;

/**
 * DELIVERY GUARANTEE strategies in RabbitMQ.
 *
 * RabbitMQ delivery is controlled by the consumer's ack/nack decisions:
 *
 *   basicAck  → "Done, delete the message."
 *   basicNack(requeue=true)  → "Failed, put it back at the top of the queue."
 *                              AT-LEAST-ONCE: message will be redelivered.
 *   basicNack(requeue=false) → "Failed, discard (or route to DLX)."
 *                              AT-MOST-ONCE: message is lost (or dead-lettered).
 *
 * This demo sends 6 messages and simulates:
 *   - Messages 1,2,3 → processed OK → ack
 *   - Message 4      → transient failure → nack(requeue=true) → redelivered once
 *   - Message 5      → poison message → nack(requeue=false) → sent to DLX/dead-letters
 *   - Message 6      → processed OK → ack
 *
 * Run: _0_InfraSetup → this class.
 * Watch dead-letters queue in http://localhost:15672
 */
public class _4_DeliveryGuaranteesDemo {

    public static void main(String[] args) throws Exception {
        // Send 6 test messages first
        try (Connection conn = _0_InfraSetup.factory().newConnection();
             Channel ch = conn.createChannel()) {

            ch.exchangeDeclare(_0_InfraSetup.DIRECT_EX, BuiltinExchangeType.DIRECT, true);
            for (int i = 1; i <= 6; i++) {
                String msg = "order-msg-" + i;
                ch.basicPublish(_0_InfraSetup.DIRECT_EX, "order",
                        MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
                System.out.println("[producer] Sent: " + msg);
            }
        }

        // Consumer with mixed ack strategy
        try (Connection conn = _0_InfraSetup.factory().newConnection();
             Channel ch = conn.createChannel()) {

            ch.basicQos(1);  // one at a time — fair dispatch
            System.out.println("[consumer] Waiting. Will simulate failures for msg-4 and msg-5.");

            boolean[] requeuedAlready = {false};

            DeliverCallback callback = (tag, delivery) -> {
                String body = new String(delivery.getBody());
                long dtag = delivery.getEnvelope().getDeliveryTag();
                System.out.println("[consumer] Got: " + body);

                if (body.contains("-4") && !requeuedAlready[0]) {
                    // AT-LEAST-ONCE: requeue — will come back for retry
                    System.out.println("[consumer] Transient failure → NACK(requeue=true)");
                    ch.basicNack(dtag, false, true);
                    requeuedAlready[0] = true;
                } else if (body.contains("-5")) {
                    // Poison message — do NOT requeue, send to DLX
                    System.out.println("[consumer] Poison message → NACK(requeue=false) → DLX");
                    ch.basicNack(dtag, false, false);
                } else {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    ch.basicAck(dtag, false);
                    System.out.println("[consumer] ACK: " + body);
                }
            };

            ch.basicConsume(_0_InfraSetup.ORDERS_Q, false, callback, t -> {});
            Thread.sleep(8_000);
        }

        System.out.println("\nCheck dead-letters queue at http://localhost:15672 — should contain order-msg-5.");
    }
}
