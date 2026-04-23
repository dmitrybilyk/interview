package com.conduct.interview._7_patterns.structural.proxy;

import java.util.HashMap;
import java.util.Map;

public class ProxyCheck2 {
    public static void main(String[] args) throws InterruptedException {
        DataLoading dataLoadingService = new DataLoadingService();
        DataLoading dataLoadingServiceProxy = new DataLoadingServiceProxy(dataLoadingService);
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
        Thread.sleep(3000);
        return "data";
    }
}

class DataLoadingServiceProxy implements DataLoading {
    private DataLoading dataLoading;
    private Map<String, String> cache;

    public DataLoadingServiceProxy(DataLoading dataLoading) {
        this.dataLoading = dataLoading;
        cache = new HashMap<>();
    }

    @Override
    public String loadData(String id) throws InterruptedException {
        String data = cache.get(id);
        if (data != null) {
            return data;
        }
        String s = dataLoading.loadData(id);
        cache.put(id, s);
        return s;
    }
}
