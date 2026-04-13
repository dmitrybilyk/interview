package com.learn.k8s.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String userName,
    String email,
    
    // Приклад кастомного формату дати
    @JsonProperty("created_at") 
    LocalDateTime createdAt
) {}