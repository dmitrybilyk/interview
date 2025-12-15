package com.conduct.interview.practise.departments;

public class EmployeeDto {

    private Long id;
    private String firstName;
    private String lastName;
    private Long departmentId;

    public EmployeeDto() {}

    public EmployeeDto(Long id, String firstName, String lastName, Long departmentId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.departmentId = departmentId;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Long getDepartmentId() {
        return departmentId;
    }
}
