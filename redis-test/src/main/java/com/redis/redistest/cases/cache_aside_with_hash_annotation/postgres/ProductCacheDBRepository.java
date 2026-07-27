package com.redis.redistest.cases.cache_aside_with_hash_annotation.postgres;

import com.redis.redistest.cases.cache_aside_with_hash_annotation.ProductCache;
import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Profile("redis-hash-annotation")
@Repository
public interface ProductCacheDBRepository extends CrudRepository<ProductCache, Long> {
    // You can even define custom finders!
    // List<ProductCache> findByName(String name);
}