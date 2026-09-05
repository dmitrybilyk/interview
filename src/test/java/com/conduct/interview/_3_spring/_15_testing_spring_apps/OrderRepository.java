package com.conduct.interview._3_spring._15_testing_spring_apps;

import java.util.Optional;

public interface OrderRepository {
    Optional<Long> findLatestOrderIdForCustomer(String customerId);
}
