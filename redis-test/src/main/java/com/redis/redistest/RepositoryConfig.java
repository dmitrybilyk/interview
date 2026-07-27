package com.redis.redistest;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.redis.redistest.cases.cache_aside_with_hash_annotation.postgres"
)
@EnableRedisRepositories(
    basePackages = "com.redis.redistest.cases.cache_aside_with_hash_annotation.redis"
)
public class RepositoryConfig {
    // This forces the JPA scanner to stay in the 'postgres' folder
    // and the Redis scanner to stay in the 'redis' folder.
}