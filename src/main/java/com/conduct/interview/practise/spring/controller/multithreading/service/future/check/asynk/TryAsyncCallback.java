package com.conduct.interview.practise.spring.controller.multithreading.service.future.check.asynk;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TryAsyncCallback {
    public static void main(String[] args) {
//        ExecutorService executorService = Executors.newFixedThreadPool(4);
        CallerService callerService = new CallerService();

        CompletableFuture<Void> completableFuture = callerService.fetchFuture("error url")
                .exceptionally(throwable -> {
                    System.out.println("failure");
                    return null;
                });

//        callerService.fetch("error url", new AsyncCallback() {
//            @Override
//            public void onSuccess() {
//                System.out.println("amazing");
//            }
//
//            @Override
//            public void onError() {
//                System.out.println("not amazing");
//            }
//        });

    }
}

interface AsyncCallback {
    void onSuccess();
    void onError();
}

class CallerService {
    CompletableFuture<Void> fetchFuture(String url) {
        return CompletableFuture.runAsync(() -> {
            System.out.println("Doing some remote call");
            if (url.contains("error")) {
                throw new RuntimeException();
            }
            System.out.println("all is good");
        });
    }

    void fetch(String url, AsyncCallback asyncCallback) {
        new Thread(() -> {
            try {
                System.out.println("Doing some remote call");
                if (url.contains("error")) {
                    throw new RuntimeException();
                }
                System.out.println("all is good");
                asyncCallback.onSuccess();
            } catch (RuntimeException e) {
                System.out.println("error");
                asyncCallback.onError();
            }
        }).start();
    }
}
