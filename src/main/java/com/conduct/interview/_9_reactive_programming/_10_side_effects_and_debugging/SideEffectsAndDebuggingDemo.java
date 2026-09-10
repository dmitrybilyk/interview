package com.conduct.interview._9_reactive_programming._10_side_effects_and_debugging;

import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.util.Logger;
import reactor.util.Loggers;

import java.util.logging.Level;

public class SideEffectsAndDebuggingDemo {

    public static void main(String[] args) {
        Flux.just(1, 2, 3)
                .doOnSubscribe(s -> System.out.println("doOnSubscribe"))
                .doOnRequest(n -> System.out.println("doOnRequest(" + n + ")"))
                .doOnNext(v -> System.out.println("doOnNext(" + v + ")"))
                .doOnComplete(() -> System.out.println("doOnComplete"))
                .subscribe();

        Flux.just(1, 2, 3)
                .doOnEach(signal -> System.out.println("doOnEach -> " + signal))
                .subscribe();

        Flux.just(1, 2, 0)
                .map(i -> 10 / i)
                .checkpoint("dividing by i")
                .onErrorResume(e -> {
                    System.out.println("error came from a checkpoint-annotated step: " + e);
                    return Flux.empty();
                })
                .subscribe();

        // .log() is a shortcut that logs every reactive Signal passing through this
        // point in the chain: onSubscribe, request(n), onNext, onComplete/onError -
        // i.e. everything the doOnX hooks above capture individually, in one operator.
        System.out.println("\n--- .log() with default category (uses the Flux's class name) ---");
        Flux.range(1, 3)
                .log()
                .subscribe();

        // Give it a name to get a readable/filterable logger category instead of the
        // generated default (handy once you have several .log() calls in one app).
        System.out.println("\n--- .log(\"category\") with a custom logger name ---");
        Flux.range(1, 3)
                .log("range.stream")
                .subscribe();

        // Restrict which signal types get logged - here only the actual data (onNext)
        // and terminal error, skipping the noisy onSubscribe/request/onComplete lines.
        // Placed after map() so the ArithmeticException actually passes through it as
        // an onError signal before onErrorResume swallows it further downstream.
        System.out.println("\n--- .log() filtered to ON_NEXT and ON_ERROR only ---");
        Flux.just(1, 2, 0)
                .map(i -> 10 / i)
                .log("division.stream", Level.INFO, false,
                        SignalType.ON_NEXT, SignalType.ON_ERROR)
                .onErrorResume(e -> Flux.empty())
                .subscribe();

        // .log() also accepts an explicit reactor.util.Logger, e.g. to route output
        // through your own logging setup instead of the default category-based one.
        System.out.println("\n--- .log(Logger) with an explicit logger instance ---");
        Logger customLogger = Loggers.getLogger("com.conduct.interview.custom");
        Flux.range(1, 2)
                .log(customLogger)
                .subscribe();
    }
}
