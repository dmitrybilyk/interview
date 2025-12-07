package com.conduct.interview.practise.departments;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(
            DepartmentRepository departmentRepository,
            EmployeeRepository employeeRepository
    ) {
        return args -> {
            if (departmentRepository.count() > 0) return;

            DepartmentEntity it = departmentRepository.save(
                    new DepartmentEntity("IT")
            );

            DepartmentEntity hr = departmentRepository.save(
                    new DepartmentEntity("HR")
            );

            employeeRepository.save(
                    new EmployeeEntity("John", "Doe", it)
            );

            employeeRepository.save(
                    new EmployeeEntity("Jane", "Smith", hr)
            );
        };
    }
}
