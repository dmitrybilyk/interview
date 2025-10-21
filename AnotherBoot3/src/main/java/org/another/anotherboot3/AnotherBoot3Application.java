package org.another.anotherboot3;

import org.another.anotherbootshared.model.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

import java.util.Random;

@SpringBootApplication
public class AnotherBoot3Application {

    public static void main(String[] args) {
        SpringApplication.run(AnotherBoot3Application.class, args);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Order> kafkaListenerContainerFactory(
            ConsumerFactory<String, Order> cf) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Order>();
        factory.setConsumerFactory(cf);


        var backOff = new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(1000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(5000L);


        var errorHandler = new DefaultErrorHandler((record, ex) -> {
            System.err.println("Giving up on record: " + record + " due to " + ex.getMessage());
        }, backOff);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }


    @KafkaListener(topics = "orders", groupId = "notif-group")
    public void listen(ConsumerRecord<String, Order> record) {
        Order order = record.value();
        System.out.println("Notification received for order: " + order);
        if (new Random().nextInt(100) < 30) {
            throw new RuntimeException("Notification failure");
        }
        System.out.println("Notification processed for order " + order.getId());
    }
}
