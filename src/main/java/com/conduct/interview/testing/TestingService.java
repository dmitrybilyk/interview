package com.conduct.interview.testing;

import java.math.BigDecimal;

public class TestingService {
    private TestingRepository testingRepository;

    public TestingService(TestingRepository testingRepository) {
        this.testingRepository = testingRepository;
    }

    public String checkAdultStatus(String studentId) {
        Student student = testingRepository.getStudent(studentId);
        return StudentUtils.isAdult(student) ? "Adult" : "Minor";
    }

    public BigDecimal calculateSalary(String studentId) {
        Student student = testingRepository.getStudent(studentId);
//        if (student.id().equals("badId")) {
//            throw new RuntimeException("Bad id: " + studentId);
//        }
        if (student.age() < 18) {
            return BigDecimal.valueOf(100);
        } else if (student.age() <= 25) {
            return BigDecimal.valueOf(500);
        } else {
            return BigDecimal.valueOf(1000);
        }
    }

    public void registerStudent(String id, String name, int age) {
        Student student = new Student(id, name, age);
        testingRepository.addStudent(student);
    }

}
