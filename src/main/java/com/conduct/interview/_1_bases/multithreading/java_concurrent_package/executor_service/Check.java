//package com.conduct.interview._1_bases.multithreading.java_concurrent_package.executor_service;
//
//import java.util.concurrent.*;
//
//public class Check {
//    public static void main(String[] args) throws InterruptedException, ExecutionException {
//        try (ExecutorService executorService = Executors.newFixedThreadPool(5)) {
//            Runnable someWork = () -> {
//                try {
//                    Thread.sleep(2000);
//                    System.out.println("Work in threed - " + Thread.currentThread().getName());
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                System.out.println("some work");
//            };
////            executorService.execute(someWork);
////            executorService.execute(someWork);
////            executorService.execute(someWork);
////            executorService.execute(someWork);
////            executorService.execute(someWork);
////            executorService.execute(someWork);
//
//            Future<String> future = executorService.submit(new Callable<String>() {
//                @Override
//                public String call() throws Exception {
//                    Thread.sleep(500);
//                    return "ddd";
//                }
//            });
//
//            Thread.sleep(1000);
//            if (future.isDone()) {
//                System.out.println("work done " + future.get());
//            } else {
//                System.out.println("Not done");
//            }
//
//        }
//    }
//}
