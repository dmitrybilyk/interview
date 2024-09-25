package com.conduct.interview._7_patterns.structural.adapter;

public class AdapterCheck {
  public static void main(String[] args) {
    JsonFile jsonFile = new JsonFile();
    SomeWebServiceAdapter service = new SomeWebServiceAdapter(new SomeWebServiceImpl());
    service.sendData(jsonFile);
  }
}

class XmlFile {
  public String type = "xml";
}

class JsonFile {
  public String type = "json";
}

interface SomeWebService {
  String sendData(XmlFile file);
}

class SomeWebServiceImpl implements SomeWebService {
  public String sendData(XmlFile file) {
    return "the data is received";
  }
}

class SomeWebServiceAdapter {
  private SomeWebService service;

  public SomeWebServiceAdapter(SomeWebService someWebService) {
    this.service = someWebService;
  }

  public String sendData(JsonFile file) {
    XmlFile jsonFile = new XmlFile();
    jsonFile.type = file.type;
    System.out.println(jsonFile.type);
    return service.sendData(jsonFile);
  }
}
