package com.conduct.interview._7_patterns.behavioral.observer;

import java.util.ArrayList;
import java.util.List;

public class ObserverPattern {
  public static void main(String[] args) {
    Observer freelancer = new Freelancer();
    Observer developer = new Developer();
    Observer qa = new QA();
    Subject site = new RssFieldsSubject();
    site.subscrube(freelancer);
    site.subscrube(developer);
    site.subscrube(qa);

    site.releaseNewArticle("Rss article");
  }
}

interface Observer {
  void update(String text);
}

class Freelancer implements Observer {
  public void update(String text) {
    System.out.println("Human is reading - " + text);
  }
}

class Developer implements Observer {
  public void update(String text) {
    System.out.println("Developer is reading - " + text);
  }
}

class QA implements Observer {
  public void update(String text) {
    System.out.println("QA is reading - " + text);
  }
}

interface Subject {
  void subscrube(Observer observer);

  void unSubscrube(Observer observer);

  void releaseNewArticle(String article);
}

class InternetSiteSubject implements Subject {
  private List<Observer> subscribers;

  public InternetSiteSubject() {
    subscribers = new ArrayList<>();
  }

  public void subscrube(Observer observer) {
    subscribers.add(observer);
  }

  public void unSubscrube(Observer observer) {
    subscribers.remove(observer);
  }

  public void releaseNewArticle(String text) {
    for (Observer observer : subscribers) {
      observer.update(text);
    }
  }
}

class RssFieldsSubject implements Subject {
  private List<Observer> subscribers;

  public RssFieldsSubject() {
    subscribers = new ArrayList<>();
  }

  public void subscrube(Observer observer) {
    subscribers.add(observer);
  }

  public void unSubscrube(Observer observer) {
    subscribers.remove(observer);
  }

  public void releaseNewArticle(String text) {
    for (Observer observer : subscribers) {
      observer.update(text);
    }
  }
}
