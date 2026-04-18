package com.conduct.interview._1_bases.java8.streams.groupingBy;

import lombok.ToString;

@ToString
public class Employee {
    private String name;
    private Department department;
    private String city;
    private double salary;

    public Employee(String name, Department department, String city, double salary) {
        this.name = name;
        this.department = department;
        this.city = city;
        this.salary = salary;
    }

    // Getters
    public String getName() { return name; }
    public Department getDepartment() { return department; }
    public String getCity() { return city; }
    public double getSalary() { return salary; }
}