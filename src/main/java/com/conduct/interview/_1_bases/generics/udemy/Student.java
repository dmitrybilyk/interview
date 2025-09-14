package com.conduct.interview._1_bases.generics.udemy;

import lombok.Builder;
import lombok.Data;
import org.hibernate.internal.util.compare.ComparableComparator;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


@Data
@Builder
public class Student implements Comparable<Student> {

    private String name;
    private int age;

    public static void main(String[] args) {
        Student student = Student.builder()
                .name("Dmytro")
                .age(44)
                .build();
        Student student1 = Student.builder()
                .name("Dmytro")
                .age(44)
                .build();

        Set<Student> studentSet = new HashSet<>();
        studentSet.add(student);
        studentSet.add(student1);
        System.out.println(studentSet);

        System.out.println(student.compareTo(student1));

    }

    @Override
    public int compareTo(@NotNull Student o) {
        return this.name.compareTo(o.name);
    }
}


class StudentFullComparator implements Comparator<Student> {

    @Override
    public int compare(Student o1, Student o2) {
        int nameCompare = o1.getName().compareTo(o2.getName());
        if (nameCompare != 0) {
            return nameCompare;
        }
        return Integer.compare(o1.getAge(), o2.getAge());
    }
}