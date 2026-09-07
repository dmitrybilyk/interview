package com.conduct.interview._13_event_driven.message_brokers.rabbitmq.examples;

import com.rabbitmq.client.*;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * FAIR DISPATCH with multiple competing consumers.
 *
 * Default RabbitMQ behavior (without basicQos): round-robin dispatch.
 * Messages are pre-fetched greedily to each consumer regardless of its speed.
 * A slow consumer hogs messages while a fast one starves.
 *
 * With basicQos(1):
 *   "Don't give me a new message until I've acked the previous one."
 *   This makes faster workers naturally pick up more messages — fair dispatch.
 *
 * To observe the difference:
 *   - SLOW_CONSUMER sleeps 800ms per message.
 *   - FAST_CONSUMER sleeps 100ms per message.
 *   - With basicQos(1), fast consumer handles ~8× more messages.
 *
 * Run: _0_InfraSetup → this class.
 */
public class _5_FairDispatchScaling {

    public static void main(String[] args) throws Exception {
        // Send 20 messages
        try (Connection conn = _0_InfraSetup.factory().newConnection();
             Channel ch = conn.createChannel()) {

            for (int i = 1; i <= 20; i++) {
                String msg = "task-" + i;
                ch.basicPublish(_0_InfraSetup.DIRECT_EX, "order",
                        MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
            }
            System.out.println("[producer] 20 tasks sent.");
        }

        AtomicInteger slowCount = new AtomicInteger();
        AtomicInteger fastCount = new AtomicInteger();

        Thread slow = workerThread("SLOW", 800, _0_InfraSetup.ORDERS_Q, slowCount);
        Thread fast = workerThread("FAST", 100, _0_InfraSetup.ORDERS_Q, fastCount);

        slow.start();
        fast.start();

        Thread.sleep(15_000);

        System.out.printf("%nFinal: SLOW processed=%d, FAST processed=%d%n",
                slowCount.get(), fastCount.get());

        slow.interrupt();
        fast.interrupt();
    }

    static Thread workerThread(String name, int sleepMs, String queue, AtomicInteger counter) {
        return new Thread(() -> {
            try (Connection conn = _0_InfraSetup.factory().newConnection();
                 Channel ch = conn.createChannel()) {

                ch.basicQos(1);  // comment this out to see greedy round-robin instead

                ch.basicConsume(queue, false, (tag, delivery) -> {
                    String body = new String(delivery.getBody());
                    try {
                        Thread.sleep(sleepMs);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    ch.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                    counter.incrementAndGet();
                    System.out.printf("[%s] Done: %s (total=%d)%n", name, body, counter.get());
                }, t -> {});

                Thread.sleep(20_000);
            } catch (Exception e) { /* shutting down */ }
        }, name);
    }
}
