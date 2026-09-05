package com.conduct.interview._9_reactive_programming._13_testing_reactive_streams;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

class ReactiveStreamsTest {

    @Test
    void assertsExactSequenceOfSignals() {
        StepVerifier.create(Flux.just(1, 2, 3).map(i -> i * 10))
                .expectNext(10, 20, 30)
                .verifyComplete();
    }

    @Test
    void assertsAnErrorInsteadOfCompletion() {
        StepVerifier.create(Mono.error(new IllegalStateException("boom")))
                .expectErrorMessage("boom")
                .verify();
    }

    @Test
    void fakesTimeInsteadOfActuallyWaiting() {
        StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofHours(1)).take(2))
                .expectSubscription()
                .thenAwait(Duration.ofHours(2)) // instant - no real waiting happens
                .expectNext(0L, 1L)
                .verifyComplete();
    }
}
