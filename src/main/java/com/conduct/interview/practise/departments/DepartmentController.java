package com.conduct.interview.practise.departments;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentDto create(@RequestBody DepartmentDto dto) {
        return service.create(dto);
    }

    @GetMapping
    public List<DepartmentDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public DepartmentDto findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public DepartmentDto update(
            @PathVariable Long id,
            @RequestBody DepartmentDto dto
    ) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
