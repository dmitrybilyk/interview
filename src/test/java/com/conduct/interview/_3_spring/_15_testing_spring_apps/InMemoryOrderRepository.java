package com.conduct.interview._3_spring._15_testing_spring_apps;

import java.util.Map;
import java.util.Optional;

/** A real (if trivial) implementation - used by the context-slice test, not a mock. */
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Long> latestOrderIdByCustomer = Map.of("customer-1", 42L);

    @Override
    public Optional<Long> findLatestOrderIdForCustomer(String customerId) {
        return Optional.ofNullable(latestOrderIdByCustomer.get(customerId));
    }
}
