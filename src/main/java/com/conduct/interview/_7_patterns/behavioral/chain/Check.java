package com.conduct.interview._7_patterns.behavioral.chain;

public class Check {
    public static void main(String[] args) {
        SomeRequest someRequest = new SomeRequest("some value");
        RequestHandler request = new Handler();
        RequestHandler request2 = new Handler2();
        RequestHandler request3 = new Handler3();
        request.setNext(request2);
        request2.setNext(request3);
        request.handle(someRequest);
    }
}

class SomeRequest {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SomeRequest(String name) {
        this.name = name;
    }
}

abstract class RequestHandler {
    protected RequestHandler next;

    public RequestHandler getNext() {
        return next;
    }

    public void setNext(RequestHandler next) {
        this.next = next;
    }

    abstract void handle(SomeRequest request);
}

class Handler extends RequestHandler {
    @Override
    public void handle(SomeRequest request) {
        System.out.println("Handling in Handler");
        if (next != null) {
            next.handle(request);
        }
    }
}

class Handler2 extends RequestHandler {
    @Override
    public void handle(SomeRequest request) {
        System.out.println("Handling in Handler2");
        if (next != null) {
            next.handle(request);
        }
    }
}

class Handler3 extends RequestHandler {
    @Override
    public void handle(SomeRequest request) {
        System.out.println("Handling in Handler3");
        if (next != null) {
            next.handle(request);
        }
    }
}
