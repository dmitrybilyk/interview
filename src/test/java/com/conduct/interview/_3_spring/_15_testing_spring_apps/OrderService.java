package com.conduct.interview._3_spring._15_testing_spring_apps;

public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public String describeLatestOrder(String customerId) {
        return orderRepository.findLatestOrderIdForCustomer(customerId)
                .map(id -> "latest order for " + customerId + " is #" + id)
                .orElse("no orders found for " + customerId);
    }
}
