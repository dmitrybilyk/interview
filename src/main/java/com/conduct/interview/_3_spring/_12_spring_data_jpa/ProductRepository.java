package com.conduct.interview._3_spring._12_spring_data_jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // no implementation anywhere - the method name alone is parsed into a JPQL query
    List<Product> findByNameContaining(String fragment);
}
