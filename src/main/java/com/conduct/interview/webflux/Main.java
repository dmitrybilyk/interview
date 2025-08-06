package com.conduct.interview.webflux;

import lombok.SneakyThrows;
import org.springframework.util.StopWatch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
//        List<Integer> names = List.of(38, 40);
//
//        Flux<Integer> flux = Flux.fromIterable(names);
//
////        flux.subscribe(System.out::println);
//
//        Flux<Long> interval = Flux.interval(Duration.ofSeconds(2));
//
//        Flux.zip(flux, interval, (integer, aLong) -> integer).subscribe(System.out::println);
//
//        Thread.sleep(7000);

        long startTime = System.currentTimeMillis();

//        Mono<String> valueFromDb = getValueFromDb();
//        Mono<String> valueFromService = getValueFromService();

        Mono<String> fromDb = Mono.defer(() -> Mono.fromCallable(Main::getValueFromDb))
                .subscribeOn(Schedulers.parallel());
        Mono<String> fromService = Mono.defer(() -> Mono.fromCallable(Main::getValueFromService))
                .subscribeOn(Schedulers.parallel());

        Mono.zip(fromDb, fromService)
//                .subscribeOn(Schedulers.parallel())
                        .subscribe(event -> {
                            long duration = System.currentTimeMillis() - startTime;

                            System.out.println("Duration: " + duration + " ms " + event.getT1() + " " +
                                    Thread.currentThread().getName());
                        });

//        Mono.zip(valueFromDb, valueFromService, (s, s2) -> s + " " + s2)
//                .subscribeOn(Schedulers.parallel())
//                .subscribe(System.out::println);

        long duration = System.currentTimeMillis() - startTime;

        System.out.println("Duration: " + duration + " ms");

        Thread.sleep(7000);

    }

    private static String getValueFromDb() {
            try {
                Thread.sleep(2000);
                System.out.println(Thread.currentThread().getName() + " in getValueFromDb");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return "Some Value from db";
    }

    private static String getValueFromService() {
        try {
            Thread.sleep(2000);
            System.out.println(Thread.currentThread().getName() + " in getValueFromService");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return "Some Value from service";
    }
}
