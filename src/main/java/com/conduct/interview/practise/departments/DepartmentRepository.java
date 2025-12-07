package com.conduct.interview.practise.departments;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository
        extends JpaRepository<DepartmentEntity, Long> {
}
