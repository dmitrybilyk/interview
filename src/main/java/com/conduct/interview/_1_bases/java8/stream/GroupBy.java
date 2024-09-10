package com.conduct.interview._1_bases.java8.stream;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.teeing;

public class GroupBy {
    public static void main(String[] args) {
//        List<String> givenList = Arrays.asList("a", "bb", "cccy", "dd");
////        List<String> result = givenList.stream()
////                .collect(collectingAndThen(toList(), ImmutableList::copyOf));
//
//        Map<Integer, List<String>> groupedByLength = givenList.stream().collect(groupingBy(String::length));
//        List<String> partitioned = givenList.stream().collect(partitioningBy(s -> s.length() > 1))
//                .get(true);
//
////        HashMap<String, Integer> result = givenList.stream().collect(teeing(
////                minBy(Integer::compareTo), // The first collector
////                maxBy(Integer::compareTo),
////                (e1, e2) -> {
////                    HashMap<String, Integer> map = new HashMap();
////                    map.put("MAX", e1.get());
////                    map.put("MIN", e2.get());
////                    return map;
////                }));// Receives the result from those collectors and combines them
//
//
        List<Employee> employeeList = Arrays.asList(
                new Employee(1, "A", 100),
                new Employee(2, "B", 200),
                new Employee(3, "C", 200),
                new Employee(4, "D", 400));

//        System.out.println(employeeList.stream().collect(Collectors.groupingBy(employee -> employee.getSalary())));

        Map<Integer, Integer> integerIntegerMap = employeeList.stream().collect(teeing(
                counting(),
                groupingBy(employee -> employee.getSalary()), (aLong, aLong2) -> {
            HashMap hashMap = new HashMap();
            hashMap.put(aLong, aLong2);
            return hashMap;}));
//        System.out.println(integerIntegerMap);

        int i = 2;
        Employee employee;
        if (i > 3) {
            employee = new Employee(3, "fdf", 55);
        } else {
            employee = null;
        }

        Optional<Employee> employee1 = Optional.ofNullable(employee);

        employee1.ifPresent(employee2 -> System.out.println("something " + employee2.getSalary()));

        Arrays.asList(3, 4, 2, 2).stream().distinct().collect(Collectors.toUnmodifiableList()).forEach(System.out::println);


        List<Employee> distinctElements = employeeList.stream().filter(distinctByKey(cust -> cust.getSalary()))
                .collect(Collectors.toList());
        System.out.println(distinctElements);


//        System.out.println(employeeList.stream().collect(groupingBy(Employee::getSalary)));
//        System.out.println(employeeList.stream().collect(teeing((Collectors.maxBy(Comparator.comparing(Employee::getSalary))),
//                Collectors.maxBy(Comparator.comparing(Employee::getSalary)), employeeList)));
//
//        HashMap<String, Employee> result = employeeList.stream().collect(
//                Collectors.teeing(
//                        Collectors.maxBy(Comparator.comparing(Employee::getSalary)),
//                        Collectors.minBy(Comparator.comparing(Employee::getSalary)),
//                        (e1, e2) -> {
//                            HashMap<String, Employee> map = new HashMap();
//                            map.put("MAX", e1.get());
//                            map.put("MIN", e2.get());
//                            return map;
//                        }
//                ));
//
//        System.out.println(result);


    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> uniqueMap = new ConcurrentHashMap<>();
        return t -> uniqueMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }
}


    @Getter
    @Setter
    @AllArgsConstructor
    @ToString
    class Employee {
        private int a;
        private String b;
        private Integer salary;
    }