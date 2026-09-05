package com.conduct.interview._9_reactive_programming._1_reactive_streams_basics;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

/**
 * Reactor's Flux already implements the Reactive Streams Publisher interface,
 * so we can talk to it using the raw org.reactivestreams types directly,
 * without any Reactor operators involved.
 */
public class ReactiveStreamsBasicsDemo {

    public static void main(String[] args) {
        Publisher<Integer> publisher = Flux.just(1, 2, 3);

        publisher.subscribe(new Subscriber<>() {
            private Subscription subscription;

            @Override
            public void onSubscribe(Subscription subscription) {
                this.subscription = subscription;
                System.out.println("onSubscribe -> requesting 1 item");
                subscription.request(1); // nothing happens until we ask for it
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("onNext(" + item + ")");
                System.out.println("  -> requesting 1 more");
                subscription.request(1);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("onError: " + t);
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
    }
}
