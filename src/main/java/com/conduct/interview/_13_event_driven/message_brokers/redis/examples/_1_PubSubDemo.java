package com.conduct.interview._13_event_driven.message_brokers.redis.examples;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

/**
 * Redis PUB/SUB — fire-and-forget broadcast.
 *
 * How it works:
 *   Publisher sends a message to a channel.
 *   All active subscribers receive it immediately.
 *   Messages are NOT stored — if no subscriber is online, the message is lost.
 *
 * Delivery guarantee: AT-MOST-ONCE.
 * Use for: live notifications, chat, real-time dashboards, cache invalidation signals.
 *
 * To try:
 *   1. docker-compose up -d (in the redis folder)
 *   2. Run this class — subscriber starts first, then publisher sends 5 messages.
 */
public class _1_PubSubDemo {

    static final String HOST    = "localhost";
    static final String CHANNEL = "events";

    public static void main(String[] args) throws InterruptedException {
        Thread subscriber = new Thread(() -> {
            try (Jedis jedis = new Jedis(HOST)) {
                System.out.println("[SUB] Subscribed to channel: " + CHANNEL);
                jedis.subscribe(new JedisPubSub() {
                    @Override
                    public void onMessage(String channel, String message) {
                        System.out.println("[SUB] Received: " + message);
                    }
                }, CHANNEL);
            }
        });
        subscriber.setDaemon(true);
        subscriber.start();

        Thread.sleep(300);  // let subscriber connect

        try (Jedis jedis = new Jedis(HOST)) {
            for (int i = 1; i <= 5; i++) {
                String msg = "{\"event\":\"ORDER_CREATED\",\"id\":" + i + "}";
                jedis.publish(CHANNEL, msg);
                System.out.println("[PUB] Published: " + msg);
                Thread.sleep(200);
            }
        }

        Thread.sleep(500);
        System.out.println("\nNote: pub/sub has NO message persistence. Stop the subscriber and republish — messages are lost.");
    }
}
