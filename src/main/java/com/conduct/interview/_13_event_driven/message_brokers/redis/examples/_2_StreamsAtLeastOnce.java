package com.conduct.interview._13_event_driven.message_brokers.redis.examples;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.XAddParams;
import redis.clients.jedis.params.XPendingParams;
import redis.clients.jedis.params.XReadGroupParams;
import redis.clients.jedis.resps.StreamEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Redis Streams — AT-LEAST-ONCE with consumer groups.
 *
 * Redis Streams are a persistent, append-only log — similar to Kafka topics but simpler.
 * Consumer groups allow multiple consumers to share work.
 * Messages are NOT deleted after delivery — they stay in the Pending Entries List (PEL)
 * until explicitly XACK'd.
 *
 * Delivery guarantee: AT-LEAST-ONCE (XACK after processing).
 *
 * To try:
 *   1. docker-compose up -d
 *   2. Run this class: producer adds 6 entries, consumer reads and ACKs them.
 *   3. Message 4 simulates a failure → not ACKed → stays in PEL.
 *      Run _3_DlqDemo to see how to handle PEL/DLQ.
 */
public class _2_StreamsAtLeastOnce {

    static final String HOST   = "localhost";
    static final String STREAM = "orders-stream";
    static final String GROUP  = "orders-group";
    static final String CONSUMER = "consumer-1";

    public static void main(String[] args) throws Exception {
        try (Jedis jedis = new Jedis(HOST)) {
            // Create consumer group (idempotent)
            try {
                jedis.xgroupCreate(STREAM, GROUP, StreamEntryID.LAST_ENTRY, true);
                System.out.println("[setup] Consumer group created.");
            } catch (Exception e) {
                System.out.println("[setup] Group already exists.");
            }

            // Producer: add 6 entries
            for (int i = 1; i <= 6; i++) {
                Map<String, String> fields = new HashMap<>();
                fields.put("orderId", String.valueOf(i));
                fields.put("amount",  String.valueOf(i * 100));
                jedis.xadd(STREAM, XAddParams.xAddParams().maxLen(1000), fields);
                System.out.println("[producer] Added order " + i);
            }

            // Consumer: read and ACK (except order 4 to simulate failure)
            // xreadGroup takes Map<String, StreamEntryID> for the streams argument
            List<Map.Entry<String, List<StreamEntry>>> results = jedis.xreadGroup(
                    GROUP, CONSUMER,
                    XReadGroupParams.xReadGroupParams().count(10).block(1000),
                    Map.of(STREAM, StreamEntryID.UNRECEIVED_ENTRY)
            );

            if (results != null) {
                for (Map.Entry<String, List<StreamEntry>> entry : results) {
                    for (StreamEntry msg : entry.getValue()) {
                        String orderId = msg.getFields().get("orderId");
                        System.out.printf("[consumer] Processing order %s (id=%s)%n", orderId, msg.getID());

                        if ("4".equals(orderId)) {
                            System.out.println("[consumer] FAILED to process order 4 — NOT acking (stays in PEL)");
                            continue;
                        }

                        jedis.xack(STREAM, GROUP, msg.getID());
                        System.out.println("[consumer] XACK order " + orderId);
                    }
                }
            }

            // Show pending entries — XPendingParams for the range overload
            var pending = jedis.xpending(STREAM, GROUP,
                    XPendingParams.xPendingParams(StreamEntryID.MINIMUM_ID, StreamEntryID.MAXIMUM_ID, 10));
            System.out.println("\n[PEL] Pending (not acked): " + pending.size() + " entries");
            pending.forEach(p -> System.out.println("  PEL entry: " + p.getID()
                    + " consumer=" + p.getConsumerName()
                    + " deliveries=" + p.getDeliveredTimes()));
        }
    }
}
