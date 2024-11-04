package com.conduct.interview.practise.spring.redis;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Cacheable(value = "products", key = "#productId")
    public Product getProductById(Long productId) {
        simulateSlowService();
        return new Product(productId, "Product " + productId);
    }

    private void simulateSlowService() {
        try {
            Thread.sleep(3000); // Simulates a delay
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
