package com.conduct.interview._9_reactive_programming._11_context_propagation;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class ContextPropagationDemo {

    private static final String REQUEST_ID = "requestId";

    public static void main(String[] args) {
        Mono<String> pipeline = Mono.deferContextual(ctx ->
                        Mono.just("handling request " + ctx.get(REQUEST_ID)))
                .map(String::toUpperCase);

        // contextWrite is placed after the operators that need to read it -
        // context visibility flows upstream, opposite to data flow.
        pipeline.contextWrite(Context.of(REQUEST_ID, "abc-123"))
                .subscribe(System.out::println);

        // Each subscription gets its own Context - safe to reuse the same
        // pipeline concurrently with different request IDs.
        pipeline.contextWrite(Context.of(REQUEST_ID, "xyz-789"))
                .subscribe(System.out::println);
    }
}
