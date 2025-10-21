package org.another.orderdeliveryservice;

import org.another.anotherbootshared.model.Order;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/deliveries")
public class DeliveryController {

    private final DeliveryRepository repo;
    private final AtomicInteger counter = new AtomicInteger();

    public DeliveryController(DeliveryRepository repo) {
        this.repo = repo;
    }

    @PostMapping
    public Delivery deliverOrder(@RequestBody Order order) {
        int number = counter.incrementAndGet();

        // Simulate slow processing
        try {
            Thread.sleep(100); // 100ms delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Delivery delivery = new Delivery(order.getId(), LocalDateTime.now(), "PREPARING");
        delivery.setStatus("DELIVERED");

        repo.save(delivery);

        System.out.printf("🚚 [REST] Delivered #%d: orderId=%s, amount=%.2f%n",
                number, order.getId(), order.getAmount());

        return delivery;
    }
}

