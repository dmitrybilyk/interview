package com.conduct.interview.practise.departments;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeServiceImpl(
            EmployeeRepository employeeRepository,
            DepartmentRepository departmentRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public EmployeeDto create(EmployeeDto dto) {
        DepartmentEntity department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        EmployeeEntity entity = new EmployeeEntity(
                dto.getFirstName(),
                dto.getLastName(),
                department
        );

        entity = employeeRepository.save(entity);

        return new EmployeeDto(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getDepartment().getId()
        );
    }

    @Override
    public List<EmployeeDto> findAll() {
        return employeeRepository.findAll()
                .stream()
                .map(e -> new EmployeeDto(
                        e.getId(),
                        e.getFirstName(),
                        e.getLastName(),
                        e.getDepartment().getId()
                ))
                .toList();
    }

    @Override
    public EmployeeDto findById(Long id) {
        EmployeeEntity e = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        return new EmployeeDto(
                e.getId(),
                e.getFirstName(),
                e.getLastName(),
                e.getDepartment().getId()
        );
    }

    @Override
    public EmployeeDto update(Long id, EmployeeDto dto) {
        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        DepartmentEntity department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setDepartment(department);

        employeeRepository.save(employee);

        return new EmployeeDto(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                department.getId()
        );
    }

    @Override
    public void delete(Long id) {
        employeeRepository.deleteById(id);
    }
}
