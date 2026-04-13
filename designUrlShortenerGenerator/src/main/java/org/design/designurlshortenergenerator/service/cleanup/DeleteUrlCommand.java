package org.design.designurlshortenergenerator.service.cleanup;

import java.time.Instant;

public record DeleteUrlCommand(
    String code, 
    Instant requestedAt,
    String requestedBy
) {}