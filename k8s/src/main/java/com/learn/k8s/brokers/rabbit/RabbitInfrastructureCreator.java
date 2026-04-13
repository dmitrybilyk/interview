package com.learn.k8s.brokers.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.HashMap;
import java.util.Map;

public class RabbitInfrastructureCreator {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            String mainEx = "my-direct-exchange";
            String mainQ = "my-rabbit-queue";
            String dlxEx = "my-dlx-exchange";
            String dlxQ = "my-dead-letters";

            // 1. Очищення: видаляємо все старе, щоб уникнути PRECONDITION_FAILED
            // ПЕРЕД ЦИМ ЗУПИНІТЬ УСІ LISTENER-И!
            channel.queueDelete(mainQ);
            channel.queueDelete(dlxQ);
            System.out.println("Step 1: Old queues deleted.");

            // 2. Створюємо Dead Letter Infrastructure
            channel.exchangeDeclare(dlxEx, "direct", true);
            channel.queueDeclare(dlxQ, true, false, false, null);
            channel.queueBind(dlxQ, dlxEx, "error-routing-key");

            // 3. Створюємо основну чергу з прив'язкою до DLX
            Map<String, Object> argsMap = new HashMap<>();
            argsMap.put("x-dead-letter-exchange", dlxEx);
            argsMap.put("x-dead-letter-routing-key", "error-routing-key");

            channel.exchangeDeclare(mainEx, "direct", true);
            channel.queueDeclare(mainQ, true, false, false, argsMap);
            channel.queueBind(mainQ, mainEx, "important-data");

            System.out.println("Step 2: Infrastructure with DLQ created successfully! 🚀");
        }
    }
}