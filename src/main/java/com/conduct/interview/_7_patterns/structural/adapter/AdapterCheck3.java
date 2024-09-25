package com.conduct.interview._7_patterns.structural.adapter;

import java.util.Arrays;
import java.util.List;

public class AdapterCheck3 {
  public static void main(String[] args) {
    SomeCoolService service = new SomeCoolService();
    SomeClientService clientService = new SomeClientService();

    List<String> listToPrint = clientService.returnsList();
    //		service.coolPrint(listToPrint);

    ServiceAdapter adapter = new DoubleCoolServiceAdapter(service);
    adapter.printListAdapted(listToPrint);
  }
}

class SomeCoolService {
  void coolPrint(String toPrint) {
    System.out.println(toPrint);
  }
}

class SomeClientService {
  List<String> returnsList() {
    return Arrays.asList("1", "2");
  }
}

interface ServiceAdapter {
  void printListAdapted(List<String> listToPrint);
}

class CoolServiceAdapter implements ServiceAdapter {
  private SomeCoolService someCoolService;

  public CoolServiceAdapter(SomeCoolService someCoolService) {
    this.someCoolService = someCoolService;
  }

  public void printListAdapted(List<String> listToPrint) {
    StringBuilder stringBuilder = new StringBuilder();
    for (String string : listToPrint) {
      stringBuilder.append(string);
    }
    someCoolService.coolPrint(stringBuilder.toString());
  }
}

class DoubleCoolServiceAdapter implements ServiceAdapter {
  private SomeCoolService someCoolService;

  public DoubleCoolServiceAdapter(SomeCoolService someCoolService) {
    this.someCoolService = someCoolService;
  }

  public void printListAdapted(List<String> listToPrint) {
    StringBuilder stringBuilder = new StringBuilder();
    for (String string : listToPrint) {
      stringBuilder.append(string);
    }
    someCoolService.coolPrint(stringBuilder.toString());
    someCoolService.coolPrint(stringBuilder.toString());
  }
}
