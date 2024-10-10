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
    firstHandler.handleRequest(someRequest);
  }
}

@Getter
@Setter
abstract class RequestHandler {
  protected RequestHandler next;

  abstract void handleRequest(SomeRequest someRequest);
}

class FirstRequestHandler extends RequestHandler {

  @Override
  void handleRequest(SomeRequest someRequest) {
    if (someRequest.getBody().contains("first")) {
      System.out.println("Handling in the first");
    }
    if (next != null) {
      next.handleRequest(someRequest);
    }
  }
}

class SecondRequestHandler extends RequestHandler {

  @Override
  void handleRequest(SomeRequest someRequest) {
    if (someRequest.getBody().contains("second")) {
      System.out.println("Handling in the second");
    }
    if (next != null) {
      next.handleRequest(someRequest);
    }
  }
}

class ThirdRequestHandler extends RequestHandler {

  @Override
  void handleRequest(SomeRequest someRequest) {
    if (someRequest.getBody().contains("third")) {
      System.out.println("Handling in the third");
    }
    if (next != null) {
      next.handleRequest(someRequest);
    }
  }
}

@Getter
@Setter
class SomeRequest {
  private String body;
}
