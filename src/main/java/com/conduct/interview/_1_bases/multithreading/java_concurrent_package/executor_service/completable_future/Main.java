package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service.completable_future;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        CompletableFuture<Student> completableFuture =
                CompletableFuture.supplyAsync(() -> {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return new Student("Dmytro");
                }).thenApply(student -> {
                    handleStudent(student);
                    return student;
                });
        completableFuture.get();
//        System.out.println(completableFuture.get());
        System.out.println("finish");


        CompletableFuture.allOf(CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("bbb");
                }),
                CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("bbb");
                })).join();
        System.out.println("fff");

        CompletableFuture<String> stringCompletableFuture = CompletableFuture.supplyAsync(() -> "Dmytro");
        System.out.println(stringCompletableFuture.get());


        CompletableFuture<String> helloFuture = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> dmytroFuture = CompletableFuture.supplyAsync(() -> "Dmytro");
        CompletableFuture<String> combinedFuture = helloFuture.thenCombine(dmytroFuture, (s, s2) -> s + s2);
        System.out.println(combinedFuture.get());

        CompletableFuture<Integer> withException = CompletableFuture.supplyAsync(() -> 10 / 0)
                .exceptionally(throwable -> 7);
        System.out.println(withException.get());

    }
    public static void handleStudent(Student student) {
        System.out.println("Handling - " + student);
    }
}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
class Student {
    private String name;
}