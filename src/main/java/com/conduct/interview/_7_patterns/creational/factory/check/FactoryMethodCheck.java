package com.conduct.interview._7_patterns.creational.factory.check;

public class FactoryMethodCheck {
    public static void main(String[] args) {
        UniversityStudentCreator studentCreator = new GoodUniversityStudentCreator();
        StudentClass student = studentCreator.createStudent();
        System.out.println(student);
    }
}

abstract class UniversityStudentCreator {
    public void supply() {
        System.out.println("something to do");
    }
    abstract StudentClass createStudent();
}

class GoodUniversityStudentCreator extends UniversityStudentCreator {

    @Override
    StudentClass createStudent() {
        return new GoodStudentClass();
    }
}

class BadUniversityStudentCreator extends UniversityStudentCreator {

    @Override
    StudentClass createStudent() {
        return new BadStudentClass();
    }
}


class StudentClass {

}

class GoodStudentClass extends StudentClass {

}

class BadStudentClass extends StudentClass {

}