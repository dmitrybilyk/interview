package com.learn.k8s.brokers.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class RabbitProducer {
    private final static String EXCHANGE_NAME = "my-direct-exchange";
    private final static String ROUTING_KEY = "important-data";

    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        // Налаштування підключення
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            // Важливо: переконуємося, що Exchange існує
            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);

            // Відправимо 10 повідомлень, щоб побачити розподіл між лісенерами
            for (int i = 1; i <= 10; i++) {
                String message = "Повідомлення номер " + i;
                
                channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes("UTF-8"));
                
                System.out.println(" [x] Відправлено: '" + message + "'");
                
                // Невелика пауза для наочності
                Thread.sleep(200);
            }
        }
    }
}