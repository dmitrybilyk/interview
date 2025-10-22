package com.conduct.interview._5_solid._3_liskov_principle;

import java.util.Arrays;
import java.util.List;

public class LiskovPrincipleCheck {
    public static void main(String[] args) {
        List<Person> personList = Arrays.asList(new Adult(), new Toddler());
        List<CrawlablePerson> crawlablePeople = Arrays.asList(new Toddler(), new Toddler());

//        if we need to move regardless of the subtype
        for (Person person : personList) {
            person.move();
        }

//        if we need just to crawl then should use specific subtype
        for (CrawlablePerson crawlablePerson : crawlablePeople) {
            crawlablePerson.crawl();
        }
    }
}

abstract class Person {
    abstract void move();
}

abstract class WalkablePerson extends Person {
    abstract void walk();

    @Override
    void move() {
        walk();
    }
}

abstract class CrawlablePerson extends Person {
    abstract void crawl();

    @Override
    void move() {
        crawl();
    }
}

class Adult extends WalkablePerson {

    @Override
    void walk() {
        System.out.println("I'm walking");
    }
}

class Toddler extends CrawlablePerson {

    @Override
    void crawl() {
        System.out.println("I'm crawling");
    }
}
