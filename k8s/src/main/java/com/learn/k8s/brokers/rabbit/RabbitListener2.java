package com.learn.k8s.brokers.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import java.util.HashMap;
import java.util.Map;

public class RabbitListener2 {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String queueName = "my-rabbit-queue";

        // ВАЖЛИВО: Аргументи мають ПОВНІСТЮ збігатися з Creator
        Map<String, Object> queueArgs = new HashMap<>();
        queueArgs.put("x-dead-letter-exchange", "my-dlx-exchange");
        queueArgs.put("x-dead-letter-routing-key", "error-routing-key");

        // Оголошуємо чергу з тими ж параметрами, що і в InfrastructureCreator
        channel.queueDeclare(queueName, true, false, false, queueArgs);

        channel.basicQos(1);

        System.out.println(" RabbitListener2 [*] Чекаю на повідомлення (з підтримкою DLQ).");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            try {
                System.out.println(" [x] Отримано: '" + message + "'");

                if (message.contains("2")) {
                    throw new RuntimeException("Симуляція помилки для повідомлення #2");
                }

                Thread.sleep(1000);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                System.out.println(" [v] Підтверджено: " + message);

            } catch (Exception e) {
                System.err.println(" [!] ПОМИЛКА: " + message + ". Відправляємо в DLQ (requeue=false).");
                // false в кінці відправляє повідомлення в DLX замість того, щоб крутити його по колу
                channel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, false);
            }
        };

        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> { });
    }
}