package com.conduct.interview.practise.spring.aop;

import org.springframework.stereotype.Service;

@Service
public class OrderService {
    public void placeOrder(String item) {
        System.out.println("Order placed for item: " + item);
    }
}