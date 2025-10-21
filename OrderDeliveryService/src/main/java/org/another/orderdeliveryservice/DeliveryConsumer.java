package org.another.orderdeliveryservice;

import org.another.anotherbootshared.model.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DeliveryConsumer {
    private final DeliveryRepository repo;

    public DeliveryConsumer(DeliveryRepository repo) {
        this.repo = repo;
    }

    @KafkaListener(topics = "orders", groupId = "delivery-group")
    public void handle(Order order) throws InterruptedException {
        if ("PAID".equals(order.getStatus())) {
            System.out.println("[DELIVERY] Preparing delivery for: " + order.getId());
            Delivery delivery = new Delivery(order.getId(), LocalDateTime.now(), "PREPARING");
            repo.save(delivery);
            Thread.sleep(500); // simulate delivery scheduling delay
        }
    }
}