package com.conduct.interview.coding.java8;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GroupingTest {
    public static void main(String[] args) {
        List<Person> people = new ArrayList<>();
        people.add(new Person("aaa", "DDD"));
        people.add(new Person("bbb", "uuu"));
        people.add(new Person("ccc", "iii"));
        people.add(new Person("aaa", "ooo"));

        Map<String, List<Person>> collect = people.stream().collect(Collectors.groupingBy(Person::getId));

        System.out.println("fdfd");
    }
}

@Getter
class Person {
    private String id;
    private String name;
    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
