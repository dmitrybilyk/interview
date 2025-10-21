package org.another.anotherboot1;

import org.another.anotherbootshared.model.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, Order> kafkaTemplate;
    private final OrderRepository repo;

    public OrderController(RestTemplate rt, KafkaTemplate<String, Order> kt, OrderRepository repo) {
        this.restTemplate = rt;
        this.kafkaTemplate = kt;
        this.repo = repo;
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        order.setId(UUID.randomUUID().toString());
        order.setStatus("PENDING");
        repo.save(order);

        boolean paid = callPaymentServiceWithRetries(order);
        if (paid) {
            order.setStatus("PAID");
            repo.save(order);
            kafkaTemplate.send("orders", order.getId(), order);
        } else {
            order.setStatus("FAILED");
            repo.save(order);
        }
        return order;
    }

    @Retryable(value = {RestClientException.class}, maxAttempts = 4, backoff = @Backoff(delay = 1000))
    public boolean callPaymentServiceWithRetries(Order order) {
        try {
            var resp = restTemplate.postForEntity("http://localhost:8092/pay", order, String.class);
            return resp.getStatusCode().is2xxSuccessful();
        } catch (RestClientException ex) {
            throw ex;
        }
    }

    @GetMapping
    public Iterable<Order> all() {
        return repo.findAll();
    }
}
