package com.conduct.interview._7_patterns.structural.decorator;

import java.util.HashMap;
import java.util.Map;

public class DecoratorCheck2 {
    public static void main(String[] args) throws InterruptedException {
        DataLoading dataLoadingService = new DataLoadingService();
        DataLoading dataLoadingServiceProxy = new DataLoadingServiceLoggingDecorator2(
                new DataLoadingServiceLoggingDecorator(dataLoadingService));
        long start = System.currentTimeMillis();
        dataLoadingServiceProxy.loadData("id");
        System.out.println("loaded first time for " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        dataLoadingServiceProxy.loadData("id");
        System.out.println("loaded second time for " + (System.currentTimeMillis() - start));
        start = System.currentTimeMillis();
        dataLoadingServiceProxy.loadData("id");
        System.out.println("loaded third time for " + (System.currentTimeMillis() - start));
    }
}

interface DataLoading {
    String loadData(String id) throws InterruptedException;
}

class DataLoadingService implements DataLoading {
    public String loadData(String id) throws InterruptedException {
        System.out.println("Loading data");
        Thread.sleep(100);
        return "data";
    }
}

class DataLoadingServiceLoggingDecorator implements DataLoading {
    private DataLoading dataLoading;

    public DataLoadingServiceLoggingDecorator(DataLoading dataLoading) {
        this.dataLoading = dataLoading;
    }

    @Override
    public String loadData(String id) throws InterruptedException {
        System.out.println("Added new logic");
        String s = dataLoading.loadData(id);
        return s;
    }
}
class DataLoadingServiceLoggingDecorator2 implements DataLoading {
    private DataLoading dataLoading;

    public DataLoadingServiceLoggingDecorator2(DataLoading dataLoading) {
        this.dataLoading = dataLoading;
    }

    @Override
    public String loadData(String id) throws InterruptedException {
        System.out.println("Added new logic2");
        String s = dataLoading.loadData(id);
        return s;
    }
}
