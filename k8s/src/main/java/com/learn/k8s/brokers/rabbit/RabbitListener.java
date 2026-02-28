package com.learn.k8s.brokers.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class RabbitListener {
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String queueName = "my-rabbit-queue";
        // Важливо: переконуємося, що черга існує
        channel.queueDeclare(queueName, true, false, false, null);

        // Обмежуємо кількість повідомлень, які Rabbit віддає одному лісенеру за раз (для чесного балансу)
        channel.basicQos(1);

        System.out.println(" [*] Чекаю на повідомлення. (Ctrl+C для виходу)");

        // Callback, який спрацює при отриманні повідомлення
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Отримано: '" + message + "'");
            
            // Імітуємо роботу
            try { Thread.sleep(1000); } catch (InterruptedException _e) {}

            // Підтверджуємо отримання (якщо не використовуємо autoAck)
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        };

        // Слухаємо чергу. autoAck = false дозволяє нам контролювати підтвердження
        channel.basicConsume(queueName, false, deliverCallback, consumerTag -> { });
    }
}