package com.conduct.interview.practise.spring.fortests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    public void testGetAllStudents() {
        Mockito.when(studentRepository.getAllStudents())
                .thenReturn(List.of(
                        new Student(1, "Student1", 22),
                        new Student(2, "Student2", 32))
                );

        List<Student> actualStudents = studentService.getStudents();

        Assertions.assertEquals(2, actualStudents.size());
    }

    @Test
    public void testCalculateSalary() {
        Assertions.assertEquals(BigDecimal.ZERO, studentService.calculateSalary());
    }

}