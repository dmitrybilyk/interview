package com.conduct.interview.practise.departments;

public class DepartmentDto {

    private Long id;
    private String name;

    public DepartmentDto() {}

    public DepartmentDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
