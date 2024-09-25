package com.conduct.interview._7_patterns.behavioral.strategy;

import static com.conduct.interview._7_patterns.behavioral.strategy.StrategyPatternCheck2.studentsDBHolder;

import java.util.List;
import java.util.stream.Collectors;
import lombok.*;

public class StrategyPatternCheck2 {
  public static StudentsDBHolder studentsDBHolder;

  static {
    Student student = new Student("Dmytro", 20);
    Student student2 = new Student("Anna", 23);
    Student student3 = new Student("Valentyn", 30);
    studentsDBHolder = new StudentsDBHolder(List.of(student, student2, student3));
  }

  public static void main(String[] args) {
    StudentFinder studentFinder = new StudentFinder(new StrategyByNameImpl());
    System.out.println(
        studentFinder.findStudents(StudentLookup.builder().name("a").age(30).build()));
  }
}

@AllArgsConstructor
class StudentFinder {
  private Strategy strategy;

  public List<Student> findStudents(StudentLookup studentLookup) {
    return strategy.execute(studentLookup);
  }
}

@Getter
@Setter
@AllArgsConstructor
@ToString
class Student {
  private String name;
  private int age;
}

@Getter
class StudentsDBHolder {
  private List<Student> studentList;

  StudentsDBHolder(List<Student> studentList) {
    this.studentList = studentList;
  }
}

@Builder
@Getter
class StudentLookup {
  private String name;
  private int age;
}

interface Strategy {
  List<Student> execute(StudentLookup studentLookup);
}

class StrategyByNameImpl implements Strategy {

  @Override
  public List<Student> execute(StudentLookup studentLookup) {
    return studentsDBHolder.getStudentList().stream()
        .filter(student -> student.getName().contains(studentLookup.getName()))
        .collect(Collectors.toList());
  }
}

class StrategyByAgeImpl implements Strategy {

  @Override
  public List<Student> execute(StudentLookup studentLookup) {
    return studentsDBHolder.getStudentList().stream()
        .filter(student -> student.getAge() == studentLookup.getAge())
        .collect(Collectors.toList());
  }
}
