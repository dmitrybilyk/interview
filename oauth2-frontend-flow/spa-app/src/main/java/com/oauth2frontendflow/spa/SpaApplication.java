package com.oauth2frontendflow.spa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Pure static file host — no OAuth logic lives here. The whole authorization_code + PKCE
 * dance happens client-side in static/app.js. See ../../oauth2/puml/SPA-FLOW(Server).puml.
 */
@SpringBootApplication
public class SpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpaApplication.class, args);
    }
}
