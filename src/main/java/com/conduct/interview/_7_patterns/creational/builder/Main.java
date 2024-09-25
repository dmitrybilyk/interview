package com.conduct.interview._7_patterns.creational.builder;

import lombok.ToString;

public class Main {
  public static void main(String[] args) {
    //        Lombok version
    //        System.out.println(Student.builder().age(30).build());
    System.out.println(new Student.StudentBuilder().withAge(20).build());
  }
}

// @Builder
@ToString
class Student {
  private String name;
  private int age;

  public Student(StudentBuilder studentBuilder) {
    this.name = studentBuilder.name;
    this.age = studentBuilder.age;
  }

  public static class StudentBuilder {
    private String name;
    private int age;

    public StudentBuilder withName(String name) {
      this.name = name;
      return this;
    }

    public StudentBuilder withAge(int age) {
      this.age = age;
      return this;
    }

    public Student build() {
      return new Student(this);
    }
  }
}
