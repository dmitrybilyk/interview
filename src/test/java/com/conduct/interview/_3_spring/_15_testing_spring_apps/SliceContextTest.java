package com.conduct.interview._3_spring._15_testing_spring_apps;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Level 2: a real, but hand-picked, slice of context - no Spring Boot autoconfiguration at all. */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = "classpath:com/conduct/interview/_3_spring/_15_testing_spring_apps/testing-context.xml")
class SliceContextTest {

    @Autowired
    private OrderService orderService;

    @Test
    void wiresARealRepositoryThroughXml() {
        assertEquals("latest order for customer-1 is #42", orderService.describeLatestOrder("customer-1"));
        assertEquals("no orders found for customer-2", orderService.describeLatestOrder("customer-2"));
    }
}
