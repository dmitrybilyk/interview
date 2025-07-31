package com.conduct.interview._1_bases.java8;

import com.rabbitmq.tools.json.JSONUtil;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//record Employee(String name, double salary) {}

public class Test {
    public static void main(String[] args) {
//        List<Integer> numbers = List.of(1, 2, 3 , 4, 5, 6, 7, 8, 9);
//        List<Integer> collected = numbers.stream()
//                .filter(value -> value % 2 != 0)
//                .toList();
//        collected.forEach(System.out::println);
//
//        List<String> list = List.of("small", "smalll");
//        list.stream()
//                .map(String::toUpperCase)
//                .toList()
//                .forEach(System.out::println);
//
//
////        Employee employee = Stream.of(
////                        new Employee("Alice", 50000),
////                        new Employee("Bob", 70000),
////                        new Employee("Charlie", 60000)
////                ).max(Comparator.comparingDouble(Employee::salary))
////                .get();
//
//
//        List<String> words = List.of("apple", "apricot", "banana", "blueberry", "cherry");
//
//        Map<Character, List<String>> grouped = words.stream().collect(Collectors.groupingBy(word -> word.charAt(0)));
//        grouped.forEach((key, value) -> System.out.println(key + " = " + value) );
//
//
//        List<Person> people = List.of(
//                new Person("Alice", 22),
//                new Person("Bob", 17),
//                new Person("Charlie", 25),
//                new Person("Diana", 16),
//                new Person("Eve", 30)
//        );
//
//        people.stream().
//                filter(person -> person.age() > 18)
//                .map(person -> person.name())
//                .toList()
//                .forEach(System.out::println);
//
//        List<Book> books = List.of(
//                new Book("Effective Java", List.of("Joshua Bloch")),
//                new Book("Java Concurrency in Practice", List.of("Brian Goetz", "Joshua Bloch")),
//                new Book("Clean Code", List.of("Robert C. Martin"))
//        );
//
//        books.stream()
//                .map(Book::authors)
//                .flatMap(Collection::stream)
//                .distinct()
//                .toList()
//                .forEach(System.out::println);
//
//
//        List<Employee> employees = List.of(
//                new Employee("Alice", "Engineering"),
//                new Employee("Bob", "HR"),
//                new Employee("Charlie", "Engineering"),
//                new Employee("David", "Marketing"),
//                new Employee("Eve", "Engineering"),
//                new Employee("Frank", "HR")
//        );
//
//        employees.stream()
//                .collect(Collectors.groupingBy(Employee::department, Collectors.counting()))
//                .forEach((key, value) -> System.out.println(key + " - " + value));
//
//    }

//        List<Person> people = List.of(
//                new Person("Alice", 22),
//                new Person("Bob", 19),
//                new Person("Charlie", 25),
//                new Person("Diana", 18)
//        );
//
//        people.stream()
//                .filter(person -> person.age() > 20)
//                .map(Person::name)
//                .collect(Collectors.joining(", "));
        List<Employee> employees = List.of(
                new Employee("Alice", 60000, 30),
                new Employee("Bob", 45000, 23),
                new Employee("Charlie", 70000, 35)
        );

        employees.stream()
                .allMatch(employee -> employee.salary() > 50000);

        Stream.iterate(new long[]{0, 1}, fib -> new long[]{fib[1], fib[0] + fib[1]})
                .limit(10)
                .map(longs -> longs[0])
                .forEach(System.out::println);

        List<String> items = List.of("apple", "banana", "apple", "orange", "banana", "kiwi");

        items.stream()
                .collect(Collectors.groupingBy(s -> s, Collectors.counting()))
                .entrySet().stream()
                .filter(stringLongEntry -> stringLongEntry.getValue() > 1)
                .map(Map.Entry::getKey)
                .toList()
                .forEach(System.out::println);

        List<Integer> numbers = List.of(1, 2, 3, 4, 5);

        int sum = numbers.stream()
                .reduce(0, Integer::sum);

        System.out.println(sum);

        List<String> words = List.of("apple", "banana", "cherry", "date", "elderberry");
        String s = words.stream()
                .reduce((s1, s2) -> s1.length() > s2.length() ? s1 : s2)
                .orElse("");
        System.out.println(s);

    }
}
record Person(String name, int age) {}

//record Employee(String name, String department) {}

//record Book(String title, List<String> authors) {}

//record Person(String name, int age) {}

record Employee(String name, int salary, int age) {}