package com.conduct.interview;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class MyServiceTest {

    @Test
    public void testCalculateSalary() {
        MyService myService = new MyService();
        Assertions.assertEquals(new BigDecimal(500), myService.calculateSalary());
    }
}
