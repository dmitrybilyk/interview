//package com.conduct.interview.testing;
//
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//
//@ExtendWith(MockitoExtension.class)
//class TestingServiceTest {
//
//    @Mock
//    private TestingRepository testingRepository;
//
//    @Captor
//    ArgumentCaptor<Student> studentCaptor;
//
//    @Spy
//    private TestingRepository testingRepositorySpy = new TestingRepository();
//
//    @InjectMocks
//    private TestingService testingService;
//
//    @Test
//    void calculateSalary() {
//        Student mockStudent = new Student("id", "name", 44);
//        when(testingRepository.getStudent(anyString()))
//                .thenReturn(mockStudent);
//        assertEquals(BigDecimal.TEN, testingService.calculateSalary("someId"));
//        verify(testingRepository).getStudent("someId");
//    }
//
//    @Test
//    void calculateSalary_shouldThrowException_forBadId() {
//        // Arrange
//        when(testingRepository.getStudent("badId"))
//                .thenThrow(new RuntimeException("Student not found"));
//
//        // Act & Assert
//        RuntimeException ex = Assertions.assertThrows(RuntimeException.class, () ->
//                testingService.calculateSalary("badId"));
//
//        assertEquals("Student not found", ex.getMessage());
//    }
//
//    @Test
//    void testCalculateSalaryWithRealRepoAndSpy() {
//        // Arrange – real method is called here!
//        testingRepositorySpy.addStudent(new Student("123", "Olga", 30));
//
//        // Act – this uses the real repository via the spy
//        BigDecimal salary = testingService.calculateSalary("123");
//
//        // Assert
//        verify(testingRepositorySpy).getStudent("123"); // <-- verify real method was called
//        verify(testingRepositorySpy).addStudent(any()); // optional
//        assert salary.equals(BigDecimal.valueOf(10));
//    }
//
//    @Test
//    void testRegisterStudent_argumentCaptor() {
//        // Act
//        testingService.registerStudent("42", "Alice", 21);
//
//        // Capture
//        verify(testingRepository).addStudent(studentCaptor.capture());
//        Student captured = studentCaptor.getValue();
//
//        // Assert captured values
//        assertEquals("42", captured.id());
//        assertEquals("Alice", captured.name());
//        assertEquals(21, captured.age());
//    }
//
//    @Test
//    void testCalculateSalaryDynamicAge() {
//        when(testingRepository.getStudent(anyString()))
//                .thenAnswer(invocation -> {
//                    String id = invocation.getArgument(0);
//                    return switch (id) {
//                        case "teen" -> new Student(id, "Timmy", 16);
//                        case "student" -> new Student(id, "Alice", 22);
//                        case "adult" -> new Student(id, "Bob", 30);
//                        default -> throw new IllegalArgumentException("Unknown id");
//                    };
//                });
//
//        Assertions.assertEquals(BigDecimal.valueOf(100), testingService.calculateSalary("teen"));
//        Assertions.assertEquals(BigDecimal.valueOf(500), testingService.calculateSalary("student"));
//        Assertions.assertEquals(BigDecimal.valueOf(1000), testingService.calculateSalary("adult"));
//    }
//
//    @Test
//    void testCheckAdultStatus_withStaticMock() {
//        // Arrange
//        Student student = new Student("42", "Alice", 17);
//        when(testingRepository.getStudent("42")).thenReturn(student);
//
//        try (MockedStatic<StudentUtils> mockedStatic = mockStatic(StudentUtils.class)) {
//            mockedStatic.when(() -> StudentUtils.isAdult(student)).thenReturn(false);
//
//            // Act
//            String status = testingService.checkAdultStatus("42");
//
//            // Assert
//            assertEquals("Minor", status);
//            mockedStatic.verify(() -> StudentUtils.isAdult(student));
//        }
//    }
//}