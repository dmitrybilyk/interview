package com.conduct.interview.practise.spring.fortests;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents() {
        return studentRepository.getAllStudents();
    }

    public BigDecimal calculateSalary() {
        return BigDecimal.ZERO;
    }
}
