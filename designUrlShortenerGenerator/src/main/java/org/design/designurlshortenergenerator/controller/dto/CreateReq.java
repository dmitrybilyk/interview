package org.design.designurlshortenergenerator.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateReq(@NotBlank String url) {}