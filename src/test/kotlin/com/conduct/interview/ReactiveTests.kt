package com.conduct.interview

import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

data class Product(val name: String)

class ReactiveTests {

    fun getProduct(): Mono<Product> {
        return Mono.just(Product("some product"))
    }

    @Test
    fun reactiveTest() {
        StepVerifier.create(getProduct())
            .expectNext(Product("some product"))
            .verifyComplete()
    }
}