package com.redis.redistest.cases.cache_aside_with_hash;

import com.redis.redistest.Product;
import com.redis.redistest.cases.cache_aside_with_string.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Profile("redis-hash")
@RestController
@RequestMapping("/api/hash/products")
public class ProductHashController {

    @Autowired
    private ProductRepository repository;

    // We still use String, String to keep Redis Insight readable
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private final String HASH_KEY_PREFIX = "product:hash:";

    /**
     * GET: Fetch full product using Hash pattern
     */
    @GetMapping("/{id}")
    public Map<Object, Object> getProduct(@PathVariable Long id) {
        String cacheKey = HASH_KEY_PREFIX + id;

        // 1. Try to get all fields from the Redis Hash
        Map<Object, Object> cachedFields = redisTemplate.opsForHash().entries(cacheKey);

        if (!cachedFields.isEmpty()) {
            System.out.println(">>> HASH HIT for product " + id);
            return cachedFields;
        }

        // 2. Cache Miss - Go to Postgres
        System.out.println(">>> HASH MISS for product " + id);
        Product product = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 3. Map Entity to a Map of Strings for Redis
        Map<String, String> map = new HashMap<>();
        map.put("id", product.getId().toString());
        map.put("name", product.getName());
        map.put("price", product.getPrice().toString());
        map.put("stock", product.getStock().toString());

        // 4. Store as Hash (HMSET) and set expiry
        redisTemplate.opsForHash().putAll(cacheKey, map);
        redisTemplate.expire(cacheKey, Duration.ofMinutes(10));

        return cachedFields.isEmpty() ?
               map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
               : cachedFields;
    }

    /**
     * PATCH: Partial Update (Just Price)
     * Demonstrates updating one field in Redis and DB
     */
    @PatchMapping("/{id}/price")
    public String updatePrice(@PathVariable Long id, @RequestParam Double newPrice) {
        String cacheKey = HASH_KEY_PREFIX + id;

        // 1. Update Database
        Product product = repository.findById(id).orElseThrow();
        product.setPrice(newPrice);
        repository.save(product);

        // 2. Partial Cache Update (HSET)
        // Instead of deleting the whole key, we just overwrite the price field.
        // If the key doesn't exist, this does nothing (or you can choose to ignore).
        Boolean exists = redisTemplate.hasKey(cacheKey);
        if (Boolean.TRUE.equals(exists)) {
            redisTemplate.opsForHash().put(cacheKey, "price", newPrice.toString());
            System.out.println(">>> Updated price in Redis Hash for " + id);
        }

        return "Price updated to " + newPrice;
    }
}