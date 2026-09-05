package com.conduct.interview._3_spring._15_testing_spring_apps;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Level 1: no Spring anywhere in this file - fastest, and usually all you need. */
class PlainUnitTestExample {

    @Test
    void needsNoContainerAtAll() {
        OrderRepository mockRepository = mock(OrderRepository.class);
        when(mockRepository.findLatestOrderIdForCustomer("customer-1")).thenReturn(Optional.of(99L));

        OrderService orderService = new OrderService(mockRepository);

        assertEquals("latest order for customer-1 is #99", orderService.describeLatestOrder("customer-1"));
    }
}
