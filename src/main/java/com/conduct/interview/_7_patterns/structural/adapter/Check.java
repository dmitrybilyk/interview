package com.conduct.interview._7_patterns.structural.adapter;

public class Check {
    public static void main(String[] args) {
        int someInteger = 10;
        SomeService someService = new SomeServiceImpl();

        SomeAdapter someAdapter = new SomeAdapter(someService);
        someAdapter.print(someInteger);
    }
}

interface SomeService {
    void print(String s);
}

class SomeServiceImpl implements SomeService {

    @Override
    public void print(String s) {
        System.out.println(s);
    }
}

class SomeAdapter {

    private final SomeService someService;

    public SomeAdapter(SomeService someService) {
        this.someService = someService;
    }

    public void print(int toPrint) {
        someService.print(String.valueOf(toPrint));
    }

}
