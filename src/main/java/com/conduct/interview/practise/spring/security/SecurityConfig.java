package com.conduct.interview.practise.spring.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // 1. Configure in-memory users
//    @Bean
//    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
//        UserDetails user1 = User.withUsername("user")
//                .password(passwordEncoder.encode("password"))
//                .roles("USER")
//                .build();
//
//        UserDetails admin = User.withUsername("admin")
//                .password(passwordEncoder.encode("admin"))
//                .roles("ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(user1, admin);
//    }

    // 2. Password encoder
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    // 3. HTTP Basic configuration
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//            .csrf(csrf -> csrf.disable()) // disable CSRF for simplicity in testing
//            .authorizeHttpRequests(auth -> auth
//                    .requestMatchers(HttpMethod.GET, "/**").authenticated()
//                    .requestMatchers(HttpMethod.HEAD, "/**").authenticated()
//                    .requestMatchers(HttpMethod.POST, "/**").authenticated()
//                    .requestMatchers(HttpMethod.PUT, "/**").authenticated()
//                    .requestMatchers(HttpMethod.DELETE, "/**").authenticated()
//                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                    .requestMatchers("/public/**").permitAll() // public endpoints
//                    .anyRequest().authenticated()
////                    .anyRequest().permitAll()
//            )
//            .httpBasic(Customizer.withDefaults()); // Enable HTTP Basic auth explicitly
//
//        return http.build();
//    }

    // 3. HTTP Basic configuration
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
////                .csrf(csrf -> csrf.enable()) // now we can keep CSRF protection on for form login
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/public/**").permitAll()
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form
//                        .loginPage("/login") // optional: your own login page
//                        .permitAll()
//                )
//                .logout(logout -> logout.permitAll());
//
//        return http.build();
//    }

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
}
