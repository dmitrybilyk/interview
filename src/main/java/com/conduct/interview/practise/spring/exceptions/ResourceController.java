package com.conduct.interview.practise.spring.exceptions;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ResourceController {

    @GetMapping("/resource/{id}")
    public String getResource(@PathVariable String id) {
        if (id.equals("0")) {
            throw new ResourceNotFoundException("Resource not found with ID: " + id);
        }
        return "Resource " + id;
    }
}
