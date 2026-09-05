package com.conduct.interview._9_reactive_programming._9_error_handling;

import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

public class ErrorHandlingDemo {

    public static void main(String[] args) {
        Flux.just(1, 2, 0, 4)
                .map(i -> 10 / i)
                .onErrorReturn(-1)
                .subscribe(v -> System.out.println("onErrorReturn -> " + v));
        // prints 10, 5, -1 then stops - the sequence ended at the error, 4 is never reached

        Flux.just(1, 2, 0, 4)
                .map(i -> 10 / i)
                .onErrorResume(e -> Flux.just(-1, -2))
                .subscribe(v -> System.out.println("onErrorResume -> " + v));
        // prints 10, 5, then switches entirely to the fallback publisher: -1, -2

        Flux.just(1, 2, 0, 4)
                .flatMap(i -> Flux.just(10 / i))
                .onErrorContinue((error, item) -> System.out.println("skipping bad item: " + item))
                .subscribe(v -> System.out.println("onErrorContinue -> " + v));
        // prints 10, 5, skips the failing element (0), then continues with the rest: 2

        AtomicInteger attempt = new AtomicInteger();
        Flux.defer(() -> {
                    int current = attempt.incrementAndGet();
                    System.out.println("attempt " + current);
                    return current < 3 ? Flux.error(new RuntimeException("boom")) : Flux.just("ok");
                })
                .retryWhen(Retry.fixedDelay(5, Duration.ofMillis(10)))
                .doOnNext(v -> System.out.println("retryWhen -> " + v))
                .blockLast(); // block so the retries (on a background scheduler) finish before main() exits
    }
}
