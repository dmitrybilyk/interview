package com.redis.redistest.cases.cache_aside_with_string;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.redistest.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@Profile("redis-string")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository repository;

    @Autowired
    private RedisTemplate<String, String> redisTemplate; // Redis tool for Strings

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) throws Exception {
        String cacheKey = "product:" + id;

        // 1. Try to fetch from Redis
        String cachedValue = redisTemplate.opsForValue().get(cacheKey);

        if (cachedValue != null) {
            System.out.println(">>> CACHE HIT for product " + id);
            return objectMapper.readValue(cachedValue, Product.class);
        }

        // 2. Cache Miss - Go to Postgres
        System.out.println(">>> CACHE MISS for product " + id + ". Fetching from DB...");
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 3. SETEX (Set with Expiry) - Save to Redis for 10 minutes
        String jsonProduct = objectMapper.writeValueAsString(product);
        redisTemplate.opsForValue().set(cacheKey, jsonProduct, Duration.ofMinutes(10));

        return product;
    }
}