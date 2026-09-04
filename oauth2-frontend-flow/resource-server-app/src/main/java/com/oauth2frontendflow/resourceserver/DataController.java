package com.oauth2frontendflow.resourceserver;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
public class DataController {

    @GetMapping("/api/protected-data")
    public Map<String, Object> protectedData(JwtAuthenticationToken auth) {
        Jwt jwt = auth.getToken();
        return Map.of(
                "message", "Резорс сервер прийняв токен напряму від браузера. Client_secret тут ніколи не існував.",
                "sub", jwt.getSubject(),
                "email", jwt.getClaimAsString("email"),
                "scope", jwt.getClaimAsString("scope"),
                "expiresAt", Instant.ofEpochSecond(jwt.getExpiresAt().getEpochSecond()).toString()
        );
    }
}
