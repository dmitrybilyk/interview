package com.learn.k8s.brokers.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.util.HashMap;
import java.util.Map;

public class RabbitListener1 {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String queueName = "my-rabbit-queue";

        // Аргументи МАЮТЬ збігатися з тими, що в InfrastructureCreator
        Map<String, Object> queueArgs = new HashMap<>();
        queueArgs.put("x-dead-letter-exchange", "my-dlx-exchange");
        queueArgs.put("x-dead-letter-routing-key", "error-routing-key");

        // Оголошуємо чергу з DLQ параметрами
        channel.queueDeclare(queueName, true, false, false, queueArgs);

        // Fair dispatch: не давати більше 1 повідомлення, поки не прийде Ack
        channel.basicQos(1);

        System.out.println(" RabbitListener1 (Швидкий) [*] Чекаю на повідомлення.");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Listener 1 отримав: '" + message + "'");

            try {
                // Listener 1 просто працює (без штучних помилок)
                Thread.sleep(500); // Працює швидше за Listener 2

                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                System.out.println(" [v] Listener 1 підтвердив: " + message);
            } catch (Exception e) {
                // Якщо раптом впаде — теж відправляємо в DLQ
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
            }
        };

        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> { });
    }
}