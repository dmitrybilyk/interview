package com.conduct.interview._7_patterns.creational.factory.check;

import lombok.Getter;
import lombok.Setter;

public class SimpleFactory {

    public static void main(String[] args) {
        Student student = createStudent("good");
        System.out.println(student.toString());
    }

    public static Student createStudent(String inputParam) {
        if (inputParam.contains("good")) {
            return new GoodStudent();
        } else {
            return new BadStudent();
        }
    }
}

@Getter
@Setter
class Student {
    private int age;
}

@Getter
@Setter
class GoodStudent extends Student {
}

@Getter
@Setter
class BadStudent extends Student {
}