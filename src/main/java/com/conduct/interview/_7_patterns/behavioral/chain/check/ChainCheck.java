package com.conduct.interview._7_patterns.behavioral.chain.check;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class ChainCheck {
    public static void main(String[] args) {
        SomeRequestHandler someRequestHandler1 = new SomeRequestHandler1();
        SomeRequestHandler someRequestHandler2 = new SomeRequestHandler2();
        SomeRequestHandler someRequestHandler3 = new SomeRequestHandler3();
        someRequestHandler1.setNext(someRequestHandler2);
        someRequestHandler2.setNext(someRequestHandler3);
        someRequestHandler1.handleRequest(new SomeRequest("Some Request 2"));
    }
}

@Getter
@Setter
@AllArgsConstructor
@ToString
class SomeRequest {
    private String content;
}

@Getter
@Setter
abstract class SomeRequestHandler {
    protected SomeRequestHandler next;
    abstract boolean handleRequest(SomeRequest someRequest);
}

class SomeRequestHandler1 extends SomeRequestHandler {

    @Override
    boolean handleRequest(SomeRequest someRequest) {
        if (someRequest.getContent().contains("1")) {
            System.out.println("Handling with 1 - " + someRequest);
            return true;
        }
        if (next != null) {
            next.handleRequest(someRequest);
        }
        return false;
    }
}


class SomeRequestHandler2 extends SomeRequestHandler {

    @Override
    boolean handleRequest(SomeRequest someRequest) {
        if (someRequest.getContent().contains("2")) {
            System.out.println("Handling with 2 - " + someRequest);
            return true;
        }
        if (next != null) {
            next.handleRequest(someRequest);
        }
        return false;
    }
}


class SomeRequestHandler3 extends SomeRequestHandler {

    @Override
    boolean handleRequest(SomeRequest someRequest) {
        if (someRequest.getContent().contains("3")) {
            System.out.println("Handling with 3 - " + someRequest);
            return true;
        }
        if (next != null) {
            next.handleRequest(someRequest);
        }
        return false;
    }
}
