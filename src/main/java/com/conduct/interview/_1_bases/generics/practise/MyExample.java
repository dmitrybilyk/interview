package com.conduct.interview._1_bases.generics.practise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MyExample {
    public static void main(String[] args) {
        List<Human> humans = List.of(new Human("aa", "bb"),
                new Human("aa2", "bb2"));
        List<HumanWithAge> humanWithAges = new ArrayList<>();
        humanWithAges.add(new HumanWithAge("aa", "bb", 10));
        humanWithAges.add(new HumanWithAge("aa2", "bb2", 20));
//        List<HumanWithAge> humansWithAge = Arrays.asList(new HumanWithAge("aa", "bb", 10),
//                new HumanWithAge("aa2", "bb2", 30));
        processPeople(humanWithAges);
    }

    private static void processPeople(List<? super HumanWithAge> humans) {
//        HumanWithAge human = humans.getFirst();
        humans.add(new HumanWithAge("dd", "dd", 3));
    }
}


class Human {
    private final String name;
    private final String surname;

    Human(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public String name() {
        return name;
    }

    public String surname() {
        return surname;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Human) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.surname, that.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname);
    }

    @Override
    public String toString() {
        return "Human[" +
                "name=" + name + ", " +
                "surname=" + surname + ']';
    }
}

class HumanWithAge extends Human{
    private int age;
    HumanWithAge(String name, String surname, int age) {
        super(name, surname);
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}

class HumanProcessor<T extends Human> {
    private List<T> humans = new ArrayList<>();

    public void addHuman(T human) {
        humans.add(human);
    }

    public void processHuman() {

    }
}