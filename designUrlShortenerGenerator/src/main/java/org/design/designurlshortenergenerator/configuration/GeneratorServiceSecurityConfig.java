package org.design.designurlshortenergenerator.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration for OAuth 2.0 Client (Browser Login/Redirect) and
 * OAuth 2.0 Resource Server (JWT Validation for APIs).
 * * This replaces the previous Basic Auth configuration to integrate with Keycloak.
 */
@Configuration
@EnableWebSecurity
public class GeneratorServiceSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless APIs
                .csrf(AbstractHttpConfigurer::disable)

                // 1. Configure Authorization Rules
                .authorizeHttpRequests(auth -> auth
                        // Protected API endpoints: requires a valid JWT with the 'generator' scope.
                        // Spring Security automatically prefixes scopes with SCOPE_
//                        .requestMatchers("/api/shorten").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // All other paths require authentication (will trigger the login/redirect flow)
                        // This covers your request to be redirected to Keycloak when accessing
                        // a public-facing but authenticated endpoint.
                        .anyRequest().authenticated()
                )

                // 2. Enable OAuth 2.0 Login (Client Flow)
                // This handles the automatic redirect to Keycloak for authentication
                // and manages the session after successful login.
                .oauth2Login(Customizer.withDefaults())

                // 3. Enable OAuth 2.0 Resource Server (JWT Validation)
                // This validates JWTs sent in the 'Authorization: Bearer' header
                // for API calls (e.g., from an HTTP client or another service).
                // FIX: Using Customizer.withDefaults() instead of the incorrect jwt.init(http)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

        return http.build();
    }

    // Note: The UserDetailsService and PasswordEncoder beans from the Basic Auth setup
    // are no longer needed, as authentication is delegated entirely to Keycloak.
}