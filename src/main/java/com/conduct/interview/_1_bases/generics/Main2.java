//package com.conduct.interview._1_bases.generics;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Main2 {
//    public static void main(String[] args) {
//        List<? extends Worker> workers = new ArrayList<>();
////        workers.add(new BaseWorker());
////        workers.add(new Worker());
////        workers.add(new HardWorker());
//        BaseWorker baseWorker = workers.get(0);
//        Worker worker = workers.get(0);
//        HardWorker hardWorker = workers.get(0);
//
////        someMethodForGenerics(workers);
//    }
//
//    private static void someMethodForGenerics(List<Worker> workers) {
//        BaseWorker baseWorker = workers.get(0);
//    }
//}
//
//class HardWorker extends Worker {}
//
//class Worker extends BaseWorker {
//
//}
//
//class BaseWorker {
//
//}