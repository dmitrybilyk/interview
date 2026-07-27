package com.redis.redistest.cases.cache_aside_with_hash_annotation.redis;

import com.redis.redistest.cases.cache_aside_with_hash_annotation.ProductCache;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Profile("redis-hash-annotation")
@Repository
public interface ProductCacheRedisRepository extends CrudRepository<ProductCache, Long> {
    // You can even define custom finders!
    // List<ProductCache> findByName(String name);
}