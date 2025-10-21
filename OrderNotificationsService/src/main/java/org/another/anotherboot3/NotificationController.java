package org.another.anotherboot3;

import org.another.anotherbootshared.model.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final List<Order> processedOrders = new CopyOnWriteArrayList<>();

    @KafkaListener(topics = "orders", groupId = "notif-group")
    public void listen(ConsumerRecord<String, Order> record) {
        Order order = record.value();
        System.out.println("Notification received for order: " + order);
        processedOrders.add(order);

        if (new Random().nextInt(100) < 30) {
            throw new RuntimeException("Simulated notification failure");
        }
        System.out.println("Processed notification for " + order.getId());
    }

    // 1️⃣ View processed Kafka orders
    @GetMapping
    public List<Order> allNotifications() {
        return processedOrders;
    }

    // 2️⃣ Clear list for test runs
    @DeleteMapping
    public void clear() {
        processedOrders.clear();
    }
}
