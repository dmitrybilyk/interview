package com.conduct.interview._7_patterns.behavioral.chain;

import lombok.NoArgsConstructor;
import lombok.Setter;

public class ChainCheck2 {
    public static void main(String[] args) {
        RequestHandler fourthRequestHandler = new FourthLevelRequestHandler(null);
        RequestHandler thirdRequestHandler = new ThirdLevelRequestHandler(fourthRequestHandler, true);
        RequestHandler secondRequestHandler = new SecondLevelRequestHandler(thirdRequestHandler, 20);
        RequestHandler firstRequestHandler = new FirstLevelRequestHandler(secondRequestHandler, "Some String");
        firstRequestHandler.baseHandle("first-second");
    }
}

@NoArgsConstructor
abstract class RequestHandler {
    protected RequestHandler next;

    public RequestHandler(RequestHandler next) {
        this.next = next;
    }

    void baseHandle(String token) {
        if (shouldHandle(token)) {
            handle(token);
        }
        if (next != null) {
            next.baseHandle(token);
        } else {
            System.out.println("Finished the handling");
        }
    }
    abstract boolean shouldHandle(String token);
    abstract void handle(String token);
}

@Setter
class FirstLevelRequestHandler extends RequestHandler {

    private String firstLevelParam;

    public FirstLevelRequestHandler(RequestHandler next) {
        super(next);
    }

    public FirstLevelRequestHandler(RequestHandler next, String firstLevelParam) {
        super(next);
        this.firstLevelParam = firstLevelParam;
    }

    @Override
    boolean shouldHandle(String token) {
        return token.contains("first");
    }

    @Override
    void handle(String token) {
        System.out.println("Handling with the First Handler with token " + token);
        System.out.println("using also additional parameter " + firstLevelParam);
    }
}

@Setter
class SecondLevelRequestHandler extends RequestHandler {

    private int secondLevelParam;

    public SecondLevelRequestHandler(RequestHandler next) {
        super(next);
    }

    public SecondLevelRequestHandler(RequestHandler next, int secondLevelParam) {
        super(next);
        this.secondLevelParam = secondLevelParam;
    }

    @Override
    boolean shouldHandle(String token) {
        return token.contains("second");
    }

    @Override
    void handle(String token) {
        System.out.println("Handling with the Second Handler with token " + token);
        System.out.println("using also additional parameter " + secondLevelParam);
    }
}

@Setter
class ThirdLevelRequestHandler extends RequestHandler {

    private boolean thirdLevelParam;

    public ThirdLevelRequestHandler(RequestHandler next) {
        super(next);
    }

    public ThirdLevelRequestHandler(RequestHandler next, boolean thirdLevelParam) {
        super(next);
        this.thirdLevelParam = thirdLevelParam;
    }

    @Override
    boolean shouldHandle(String token) {
        return token.contains("third");
    }

    @Override
    void handle(String token) {
        System.out.println("Handling with the Third Handler with token " + token);
        System.out.println("using also additional parameter " + thirdLevelParam);
    }
}

@Setter
class FourthLevelRequestHandler extends RequestHandler {

    public FourthLevelRequestHandler(RequestHandler next) {
        super(next);
    }

    @Override
    boolean shouldHandle(String token) {
        return token.contains("fourth");
    }

    @Override
    void handle(String token) {
        System.out.println("Handling with the Fourth Handler with token " + token);
        System.out.println("No additional parameters are used");
    }
}
