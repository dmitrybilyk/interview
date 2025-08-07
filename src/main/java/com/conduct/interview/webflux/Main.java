package com.conduct.interview.webflux;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;


public class Main {

    public static void main(String[] args) throws InterruptedException {

        long startTime = System.currentTimeMillis();

        Mono<String> fromDb = Mono.defer(() -> Mono.fromCallable(Main::getValueFromDb))
                .subscribeOn(Schedulers.parallel());
        Mono<String> fromService = Mono.defer(() -> Mono.fromCallable(Main::getValueFromService))
                .subscribeOn(Schedulers.parallel());

        Mono.zip(fromDb, fromService)
                        .subscribe(event -> {
                            long duration = System.currentTimeMillis() - startTime;

                            System.out.println("Duration: " + duration + " ms " + event.getT1() + " " +
                                    Thread.currentThread().getName());
                        });

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
