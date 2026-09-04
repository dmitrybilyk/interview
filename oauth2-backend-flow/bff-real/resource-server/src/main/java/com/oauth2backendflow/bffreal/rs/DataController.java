package com.oauth2backendflow.bffreal.rs;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DataController {

    @GetMapping("/api/protected-data")
    public Map<String, Object> protectedData(JwtAuthenticationToken auth) {
        Jwt jwt = auth.getToken();
        return Map.of(
                "message", "Привіт від Resource Server! Я перевірив підпис JWT проти bff-real-realm і пустив тебе.",
                "sub", jwt.getSubject(),
                "email", jwt.getClaimAsString("email"),
                "scope", jwt.getClaimAsString("scope"),
                "issuer", jwt.getIssuer().toString()
        );
    }
}
