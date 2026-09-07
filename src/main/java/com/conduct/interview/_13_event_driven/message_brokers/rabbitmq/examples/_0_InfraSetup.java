package com.conduct.interview._13_event_driven.message_brokers.rabbitmq.examples;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Run FIRST. Creates all exchanges, queues, and bindings used by other examples.
 *
 * Topology:
 *
 *   direct-exchange ──(routing-key: order)──→ orders-queue ──┐ (DLX on reject)
 *   topic-exchange  ──(logs.*)──────────────→ logs-queue   │
 *   fanout-exchange ──(all)──────────────────→ fanout-q-1  │
 *                                          └──→ fanout-q-2  │
 *   dlx-exchange    ──(dead)─────────────────→ dead-letters ←┘
 *
 * After running: open http://localhost:15672 (guest/guest) → Exchanges / Queues.
 */
public class _0_InfraSetup {

    static final String HOST = "localhost";

    // Exchanges
    static final String DIRECT_EX = "demo-direct";
    static final String TOPIC_EX  = "demo-topic";
    static final String FANOUT_EX = "demo-fanout";
    static final String DLX       = "demo-dlx";

    // Queues
    static final String ORDERS_Q     = "orders-queue";
    static final String LOGS_Q       = "logs-queue";
    static final String FANOUT_Q1    = "fanout-q-1";
    static final String FANOUT_Q2    = "fanout-q-2";
    static final String DEAD_LETTERS = "dead-letters";

    public static void main(String[] args) throws Exception {
        try (Connection conn = factory().newConnection();
             Channel ch = conn.createChannel()) {

            // Dead-letter infrastructure
            ch.exchangeDeclare(DLX, BuiltinExchangeType.DIRECT, true);
            ch.queueDeclare(DEAD_LETTERS, true, false, false, null);
            ch.queueBind(DEAD_LETTERS, DLX, "dead");
            System.out.println("DLX created.");

            // Direct exchange + orders queue (with DLX wired)
            ch.exchangeDeclare(DIRECT_EX, BuiltinExchangeType.DIRECT, true);
            Map<String, Object> ordersArgs = new HashMap<>();
            ordersArgs.put("x-dead-letter-exchange",    DLX);
            ordersArgs.put("x-dead-letter-routing-key", "dead");
            ch.queueDeclare(ORDERS_Q, true, false, false, ordersArgs);
            ch.queueBind(ORDERS_Q, DIRECT_EX, "order");
            System.out.println("Direct exchange + orders-queue created.");

            // Topic exchange + logs queue
            ch.exchangeDeclare(TOPIC_EX, BuiltinExchangeType.TOPIC, true);
            ch.queueDeclare(LOGS_Q, true, false, false, null);
            ch.queueBind(LOGS_Q, TOPIC_EX, "logs.*");   // matches logs.info, logs.error, etc.
            System.out.println("Topic exchange + logs-queue created.");

            // Fanout exchange + two queues
            ch.exchangeDeclare(FANOUT_EX, BuiltinExchangeType.FANOUT, true);
            ch.queueDeclare(FANOUT_Q1, true, false, false, null);
            ch.queueDeclare(FANOUT_Q2, true, false, false, null);
            ch.queueBind(FANOUT_Q1, FANOUT_EX, "");  // fanout ignores routing key
            ch.queueBind(FANOUT_Q2, FANOUT_EX, "");
            System.out.println("Fanout exchange + fanout-q-1, fanout-q-2 created.");

            System.out.println("\nAll infrastructure ready. Open http://localhost:15672");
        }
    }

    static ConnectionFactory factory() {
        ConnectionFactory f = new ConnectionFactory();
        f.setHost(HOST);
        f.setUsername("guest");
        f.setPassword("guest");
        return f;
    }
}
