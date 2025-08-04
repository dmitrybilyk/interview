package com.conduct.interview.testing;

import java.util.HashMap;
import java.util.Map;

public class TestingRepository {
    private Map<String, Student> students = new HashMap<>();

    public void addStudent(Student student) {
        students.put(student.id(), student);
    }

    public Student getStudent(String studentId) {
        return students.get(studentId);
    }
}
