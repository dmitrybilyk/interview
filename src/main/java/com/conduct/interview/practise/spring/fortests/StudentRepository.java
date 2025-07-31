package com.conduct.interview.practise.spring.fortests;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StudentRepository {
    private Map<Integer, Student> students = new HashMap<>();

    public Student getStudent(int id) {
        return students.get(id);
    }

    public void addStudent(Student student) {
        students.put(student.id(), student);
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students.values());
    }

}
