package com.conduct.interview._1_bases.generics.practise.check;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Company<Worker, String> company = new Company<>();
        company.addWorker(new GoodWorker("Tom", "Profession"));
        company.addWorker(new BadWorker("ATom2", "Profession2"));

        company.getWorkers().forEach(System.out::println);

        company.findWorkers(worker -> worker.getName().equals("Tom"))
                .forEach(System.out::println);
        
        List<String> workers = company.mapWorkers(Worker::getName);
        workers.forEach(System.out::println);

        Company<Worker, String> company2 = new Company<>();
        CompanyUtils.copyWorkers(company, company2);
        company2.getWorkers().forEach(System.out::println);

        List<String> workers2 = company2.exportWorkers(worker -> " exported - " + worker.getName());
        List<String> workers3 = company2.exportWorkers(new DoubleNameExporter());
        workers3.forEach(System.out::println);
    }
}
