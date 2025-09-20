package com.conduct.interview._7_patterns.structural.bridge;

public class BridgeCheck {
    public static void main(String[] args) {
        Person person = new FoulPerson(new WhiteRace());
        person.cure();
    }
}

abstract class Person {
    private Race race;

    public Person(Race race) {
        this.race = race;
    }

    void cure() {
        race.showRaceTreats();
        System.out.println("curing the person of race " + race.getClass().getSimpleName());
    }
}

class SmartPerson extends Person {
    public SmartPerson(Race race) {
        super(race);
    }

    @Override
    void cure() {
        super.cure();
        System.out.println("Smart person cure");
    }
}

class FoulPerson extends Person {
    public FoulPerson(Race race) {
        super(race);
    }

    @Override
    void cure() {
        super.cure();
        System.out.println("Foul person cure");
    }
}

interface Race {
    void showRaceTreats();
}

class BlackRace implements Race {

    @Override
    public void showRaceTreats() {
        System.out.println("Black race");
    }
}

class WhiteRace implements Race {

    @Override
    public void showRaceTreats() {
        System.out.println("White race");
    }
}