package com.conduct.interview._7_patterns.creational.factory;

public class Main {
  public static void main(String[] args) {
    String inParam = "student";

    //        Simple Factory Usage
    //        Human human = HumanSimpleFactory.createHuman(inParam);

    //        Factory method usage
    //        HumanCreator humanCreator;
    //        if(inParam.equals("student")) {
    //            humanCreator = new StudentCreator();
    //        } else if (inParam.equals("Pupil")){
    //            humanCreator = new PupilCreator();
    //        } else {
    //            humanCreator = new ChildCreator();
    //        }
    //        Human human = humanCreator.createHuman();

    //        Abstract Factory usage
    //        PlanetAbstractFactory abstractFactory = new PlanetEarthAbstractFactory();
    PlanetAbstractFactory abstractFactory = new PlanetMarsAbstractFactory();
    Human child = abstractFactory.createChild();
    System.out.println(child);
    Human student = abstractFactory.createStudent();
    System.out.println(student);
    Human pupil = abstractFactory.createPupil();
    System.out.println(pupil);
  }
}

class Human {}

class Student extends Human {}

class Pupil extends Human {}

class Child extends Human {}

class HumanSimpleFactory {
  public static Human createHuman(String inParam) {
    if (inParam.equals("student")) {
      return new Student();
    } else if (inParam.equals("Pupil")) {
      return new Pupil();
    } else {
      return new Child();
    }
  }
}

abstract class HumanCreator {
  public abstract Human createHuman();
}

class StudentCreator extends HumanCreator {

  @Override
  public Human createHuman() {
    return new Student();
  }
}

class PupilCreator extends HumanCreator {

  @Override
  public Human createHuman() {
    return new Pupil();
  }
}

class ChildCreator extends HumanCreator {

  @Override
  public Human createHuman() {
    return new Child();
  }
}

abstract class PlanetAbstractFactory {
  abstract Student createStudent();

  abstract Pupil createPupil();

  abstract Child createChild();
}

class PlanetEarthAbstractFactory extends PlanetAbstractFactory {

  @Override
  Student createStudent() {
    System.out.println("Creating student on the planet Earth");
    return new Student();
  }

  @Override
  Pupil createPupil() {
    System.out.println("Creating pupil on the planet Earth");
    return new Pupil();
  }

  @Override
  Child createChild() {
    System.out.println("Creating child on the planet Earth");
    return new Child();
  }
}

class PlanetMarsAbstractFactory extends PlanetAbstractFactory {

  @Override
  Student createStudent() {
    System.out.println("Creating student on the planet Mars");
    return new Student();
  }

  @Override
  Pupil createPupil() {
    System.out.println("Creating pupil on the planet Mars");
    return new Pupil();
  }

  @Override
  Child createChild() {
    System.out.println("Creating child on the planet Mars");
    return new Child();
  }
}
