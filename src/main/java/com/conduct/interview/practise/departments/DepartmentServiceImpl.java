package com.conduct.interview.practise.departments;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository repository;

    public DepartmentServiceImpl(DepartmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public DepartmentDto create(DepartmentDto dto) {
        DepartmentEntity entity = new DepartmentEntity(dto.getName());
        entity = repository.save(entity);
        return new DepartmentDto(entity.getId(), entity.getName());
    }

    @Override
    public List<DepartmentDto> findAll() {
        return repository.findAll()
                .stream()
                .map(d -> new DepartmentDto(d.getId(), d.getName()))
                .toList();
    }

    @Override
    public DepartmentDto findById(Long id) {
        DepartmentEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        return new DepartmentDto(entity.getId(), entity.getName());
    }

    @Override
    public DepartmentDto update(Long id, DepartmentDto dto) {
        DepartmentEntity entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        entity.setName(dto.getName());
        entity = repository.save(entity);

        return new DepartmentDto(entity.getId(), entity.getName());
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
