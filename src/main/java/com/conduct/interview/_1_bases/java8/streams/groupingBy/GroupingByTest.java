package com.conduct.interview._1_bases.java8.streams.groupingBy;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class GroupingByTest {
    public static void main(String[] args) {
//        List<String> names = List.of("Alice", "Bob", "Charlie", "Amy", "Bill");
//
//        Map<Character, List<String>> groupedByFirstLetter = names.stream()
//                .collect(Collectors.groupingBy(name -> name.charAt(0)));
//
//        System.out.println(groupedByFirstLetter);
//
//// Result: {A=[Alice, Amy], B=[Bob, Bill], C=[Charlie]}
//
//        List<Integer> integerList = List.of(4, 5, 7, 3, 2);
//        System.out.println(
//                integerList
//                        .stream()
//                        .collect(Collectors.partitioningBy(intValue -> intValue > 4))
//        );
//
        List<Employee> employees = List.of(
                new Employee("Dmytro", Department.FINANCE, "Horlivka", 44.0),
                new Employee("Dmytro2", Department.IT, "Horlivka", 544.0),
                new Employee("Dmytro7", Department.IT, "Horlivka", 544.0),
                new Employee("Dmytro3", Department.IT, "Kharkiv", 144.0),
                new Employee("Dmytro4", Department.FINANCE, "Lviv", 440.0)
        );

        Map<Department, List<Employee>> departmentListMap =
                employees.stream()
                        .collect(Collectors.groupingBy(Employee::getDepartment));

        departmentListMap.forEach((dept, empList) -> {
            System.out.println("\n" + dept); // Department name in CAPS
            empList.forEach(emp ->
                    System.out.printf("  └── %-10s | %-10s | $%.2f%n",
                            emp.getName(), emp.getCity(), emp.getSalary())
            );
        });

        departmentListMap.computeIfAbsent(Department.MARKETING, k -> new ArrayList<>())
                .add(new Employee("fff", Department.MARKETING, "fff", 44.0));

//
//        Map<Department, Map<String, List<Employee>>> complexMap = employees.stream()
//                .collect(Collectors.groupingBy(Employee::getDepartment,
//                        Collectors.groupingBy(Employee::getCity)));
//
//        complexMap.forEach((dept, cityMap) -> {
//            System.out.println("\n" + dept); // Department Header
//            cityMap.forEach((city, empList) -> {
//                System.out.println("  └── " + city); // City Sub-header
//                empList.forEach(emp ->
//                        System.out.printf("      • %-10s ($%.2f)%n", emp.getName(), emp.getSalary())
//                );
//            });
//        });

//        TreeMap<Department, Double> collect = employees.stream()
//                .collect(Collectors.groupingBy(
//                        Employee::getDepartment,
//                        () -> new TreeMap<>(Comparator.reverseOrder()),
//                        Collectors.summingDouble(Employee::getSalary)
//                ));
//        System.out.println(collect);

    }
}
