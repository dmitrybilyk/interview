package com.learn.k8s.brokers.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitInfrastructureCreator {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            String exchangeName = "my-direct-exchange";
            String queueName = "my-rabbit-queue";
            String routingKey = "important-data";

            // 1. Створюємо Exchange (тип direct - доставка по точному співпадінню ключа)
            channel.exchangeDeclare(exchangeName, "direct", true);

            // 2. Створюємо Queue (durable=true, щоб черга не зникла після рестарту Rabbit)
            channel.queueDeclare(queueName, true, false, false, null);

            // 3. Зв'язуємо чергу з обмінником (Binding)
            channel.queueBind(queueName, exchangeName, routingKey);

            System.out.println("Інфраструктура RabbitMQ готова! 🐰");
        }
    }
}