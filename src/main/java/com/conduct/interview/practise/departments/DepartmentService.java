package com.conduct.interview.practise.departments;

import java.util.List;

public interface DepartmentService {

    DepartmentDto create(DepartmentDto dto);

    List<DepartmentDto> findAll();

    DepartmentDto findById(Long id);

    DepartmentDto update(Long id, DepartmentDto dto);

    void delete(Long id);
}
