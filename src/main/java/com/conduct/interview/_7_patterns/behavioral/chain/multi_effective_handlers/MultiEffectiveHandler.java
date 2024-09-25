package com.conduct.interview._7_patterns.behavioral.chain.multi_effective_handlers;

import lombok.Getter;
import lombok.Setter;

public class MultiEffectiveHandler {
  public static void main(String[] args) {
    SomeRequest someRequest = new SomeRequest();
    someRequest.setBody("My lovely body - second, first,third");

    RequestHandler thirdHandler = new ThirdRequestHandler();
    RequestHandler secondHandler = new SecondRequestHandler();
    secondHandler.setNext(thirdHandler);
    RequestHandler firstHandler = new FirstRequestHandler();
    firstHandler.setNext(secondHandler);
    System.out.println("Request is handled with - " + firstHandler.handleRequest(someRequest));
  }
}

@Getter
@Setter
abstract class RequestHandler {
  protected RequestHandler next;

  abstract boolean handleRequest(SomeRequest someRequest);
}

class FirstRequestHandler extends RequestHandler {

  @Override
  boolean handleRequest(SomeRequest someRequest) {
    if (someRequest.getBody().contains("first")) {
      System.out.println("Handling in the first");
    }
    if (next != null) {
      return next.handleRequest(someRequest);
    }
    return false;
  }
}

class SecondRequestHandler extends RequestHandler {

  @Override
  boolean handleRequest(SomeRequest someRequest) {
    if (someRequest.getBody().contains("second")) {
      System.out.println("Handling in the second");
    }
    if (next != null) {
      return next.handleRequest(someRequest);
    }
    return false;
  }
}

class ThirdRequestHandler extends RequestHandler {

  @Override
  boolean handleRequest(SomeRequest someRequest) {
    if (someRequest.getBody().contains("third")) {
      System.out.println("Handling in the third");
    }
    if (next != null) {
      return next.handleRequest(someRequest);
    }
    return false;
  }
}

@Getter
@Setter
class SomeRequest {
  private String body;
}
