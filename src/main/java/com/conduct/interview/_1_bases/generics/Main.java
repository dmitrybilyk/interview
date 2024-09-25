package com.conduct.interview._1_bases.generics;

import java.util.ArrayList;
import java.util.List;

public class Main {
  public static void main(String[] args) {
    //        Can't: Java is invariant
    //        List<Human> humans = new ArrayList<Student>();

    //        List<Human> humanList = new ArrayList<>();
    //        List<Student> students = new ArrayList<>();
    //        Can't: Java is invariant
    //        humanList = students;

    List<Person> people = new ArrayList<>();
    people.add(new Student());
    people.add(new Person());
    //        people.add(new Human());
    strictGenericType(people);

    List<Student> students = new ArrayList<>();
    students.add(new Student());
    students.add(new GoodStudent());

    covariance(students);

    List<Person> personList = new ArrayList<>();
    personList.add(new Student());
    contrVariance(personList);
  }

  private static void strictGenericType(List<Person> people) {
    Person person = people.get(0);
    Human human = people.get(0);
    //        Student student = people.get(0);
    people.add(new Student());
    people.add(new Person());
    //        people.add(new Human());
  }

  private static void covariance(List<? extends Person> people) {
    Person person = people.get(0);
    Human human = people.get(0);
    //        Student student = people.get(0);

    //        people.add(new Student());
  }

  private static void contrVariance(List<? super Person> people) {
    people.add(new Student());
    //        people.add(new Human());

    Object person = people.get(0);
    Object human = people.get(0);
  }
}

class Human {}

class Person extends Human {}

class Student extends Person {}

class GoodStudent extends Student {}
