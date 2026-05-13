package com.redis.redistest.cases.cache_aside_with_hash_annotation;

import com.redis.redistest.cases.cache_aside_with_hash_annotation.postgres.ProductCacheDBRepository;
import com.redis.redistest.cases.cache_aside_with_hash_annotation.redis.ProductCacheRedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

@Profile("redis-hash-annotation")
@RestController
@RequestMapping("/api/repo/products")
public class ProductRepoController {

    @Autowired
    private ProductCacheDBRepository postgresRepo;

    @Autowired
    private ProductCacheRedisRepository redisRepo;

    @GetMapping("/{id}")
    public ProductCache getProduct(@PathVariable Long id) {
        // 1. Look in Redis Repository
        return redisRepo.findById(id).orElseGet(() -> {
            System.out.println(">>> Cache Miss in Repository!");
            
            // 2. Load from Postgres
            ProductCache pgProduct = postgresRepo.findById(id).orElseThrow();

            // 3. Convert and Save to Redis
            ProductCache cache = new ProductCache();
            cache.setId(pgProduct.getId());
            cache.setName(pgProduct.getName());
            cache.setPrice(pgProduct.getPrice());
            cache.setStock(pgProduct.getStock());

            return redisRepo.save(cache);
        });
    }

    /**
     * POST: Add a new product
     * Saves to Postgres first, then seeds the Redis cache.
     */
    @PostMapping
    public ProductCache addProduct(@RequestBody ProductCache newProduct) {
        // 1. Save to PostgreSQL (Primary Storage)
        // We ensure the ID is null so Postgres generates a new one
        newProduct.setId(null);
        ProductCache savedProduct = postgresRepo.save(newProduct);
        System.out.println(">>> Saved to Postgres with ID: " + savedProduct.getId());

        // 2. Save to Redis (Pre-seeding the cache)
        // This is optional; you could also just wait for the first GET request.
        redisRepo.save(savedProduct);
        System.out.println(">>> Pre-seeded Redis cache for ID: " + savedProduct.getId());

        return savedProduct;
    }
}