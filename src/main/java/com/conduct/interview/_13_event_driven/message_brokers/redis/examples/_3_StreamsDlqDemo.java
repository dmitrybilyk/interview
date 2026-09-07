package com.conduct.interview._13_event_driven.message_brokers.redis.examples;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.params.XAddParams;
import redis.clients.jedis.params.XAutoClaimParams;
import redis.clients.jedis.params.XPendingParams;
import redis.clients.jedis.resps.StreamEntry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Redis Streams — DEAD-LETTER QUEUE pattern.
 *
 * Redis has no native DLQ. We implement it via the Pending Entries List (PEL):
 *   1. Consumer reads a message but processing fails → does NOT XACK.
 *   2. Message stays in PEL (pending for this consumer group).
 *   3. A "claim" loop (XAUTOCLAIM) periodically reclaims messages
 *      that have been pending longer than a threshold (idle time).
 *   4. After N delivery attempts → move to a dead-letter stream.
 *
 * Flow:
 *   orders-stream ──[fail]──→ PEL ──[retry]──→ orders-stream (reclaim)
 *                                  ──[max retries]──→ orders-dlq-stream
 *
 * To try:
 *   1. Run _2_StreamsAtLeastOnce first (puts order 4 in PEL).
 *   2. Run this class — it claims the stuck message, retries once, then DLQs it.
 */
public class _3_StreamsDlqDemo {

    static final String HOST       = "localhost";
    static final String STREAM     = "orders-stream";
    static final String DLQ_STREAM = "orders-dlq-stream";
    static final String GROUP      = "orders-group";
    static final String CONSUMER   = "dlq-processor";

    static final int  MAX_RETRIES = 2;
    static final long IDLE_MS     = 500;

    public static void main(String[] args) throws Exception {
        try (Jedis jedis = new Jedis(HOST)) {
            System.out.println("[DLQ] Scanning PEL for stuck messages...");

            // xautoclaim returns Map.Entry<StreamEntryID, List<StreamEntry>>
            // — key: next cursor, value: list of claimed entries
            Map.Entry<StreamEntryID, List<StreamEntry>> claimed = jedis.xautoclaim(
                    STREAM, GROUP, CONSUMER,
                    IDLE_MS,
                    StreamEntryID.MINIMUM_ID,
                    XAutoClaimParams.xAutoClaimParams().count(10)
            );

            List<StreamEntry> entries = claimed.getValue();
            System.out.println("[DLQ] Claimed " + entries.size() + " stuck messages.");

            for (StreamEntry msg : entries) {
                String orderId   = msg.getFields().get("orderId");
                long  deliveries = getDeliveryCount(jedis, msg.getID());
                System.out.printf("[DLQ] Stuck: orderId=%s id=%s deliveries=%d%n",
                        orderId, msg.getID(), deliveries);

                if (deliveries >= MAX_RETRIES) {
                    Map<String, String> dlqFields = new HashMap<>(msg.getFields());
                    dlqFields.put("original-id",    msg.getID().toString());
                    dlqFields.put("error",           "max retries exceeded");
                    dlqFields.put("delivery-count",  String.valueOf(deliveries));
                    jedis.xadd(DLQ_STREAM, XAddParams.xAddParams().maxLen(1000), dlqFields);
                    jedis.xack(STREAM, GROUP, msg.getID());
                    System.out.println("[DLQ] Moved to DLQ stream: " + orderId);
                } else {
                    System.out.println("[DLQ] Will retry orderId=" + orderId + " (attempt " + deliveries + ")");
                    jedis.xack(STREAM, GROUP, msg.getID());
                }
            }

            // Show DLQ stream contents
            List<StreamEntry> dlqEntries = jedis.xrange(DLQ_STREAM, StreamEntryID.MINIMUM_ID, StreamEntryID.MAXIMUM_ID);
            System.out.println("\n[DLQ stream] Contents (" + dlqEntries.size() + " entries):");
            dlqEntries.forEach(e -> System.out.println("  " + e.getFields()));
        }
    }

    static long getDeliveryCount(Jedis jedis, StreamEntryID id) {
        // XPendingParams lets us filter by ID range within the group
        var pending = jedis.xpending(STREAM, GROUP,
                XPendingParams.xPendingParams(id, id, 1));
        return pending.isEmpty() ? 0 : pending.get(0).getDeliveredTimes();
    }
}
