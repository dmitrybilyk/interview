package com.conduct.interview.practise.spring.transactions

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import javax.sql.DataSource
import org.springframework.boot.jdbc.DataSourceBuilder

@Configuration
@Profile("postgres")
open  // This bean is only loaded for the 'postgres' profile
class DatabaseConfig {

    @Bean
    open fun dataSource(): DataSource {
        return DataSourceBuilder.create()
            .url("jdbc:postgresql://localhost:5432/mydb")
            .username("dmytro")
            .password("password")
            .build()
    }
}
