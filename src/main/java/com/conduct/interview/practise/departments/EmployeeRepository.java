package com.conduct.interview.practise.departments;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository
        extends JpaRepository<EmployeeEntity, Long> {
}
