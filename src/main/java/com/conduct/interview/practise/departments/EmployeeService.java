package com.conduct.interview.practise.departments;

import java.util.List;

public interface EmployeeService {

    EmployeeDto create(EmployeeDto dto);

    List<EmployeeDto> findAll();

    EmployeeDto findById(Long id);

    EmployeeDto update(Long id, EmployeeDto dto);

    void delete(Long id);
}
