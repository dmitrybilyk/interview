package com.redis.redistest.cases.cache_aside_with_string;

import com.redis.redistest.Product;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Profile("redis-string")
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Standard CRUD methods are built-in:
    // findById(Long id)
    // save(Product product)
    // deleteById(Long id)
}