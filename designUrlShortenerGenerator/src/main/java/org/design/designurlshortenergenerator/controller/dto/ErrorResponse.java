package org.design.designurlshortenergenerator.controller.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(
    int status,
    String message,
    LocalDateTime timestamp,
    Map<String, String> errors // Для деталізації помилок валідації (поля)
) {}