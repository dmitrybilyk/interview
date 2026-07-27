package com.aouth2.authclient.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
@Profile("!auth-client")
// Ця конфігурація буде активна, якщо не вибрано жодного профілю (default)
public class DefaultSecurityConfig {

    private final ClientRegistrationRepository clientRegistrationRepository;

    public DefaultSecurityConfig(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("dima")
                        .password("{noop}12345") // {noop} для plain text
                        .roles("USER")
                        .build()
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
            // 1. Управління запитами: робимо головну та статичні ресурси публічними
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/error", "/webjars/**").permitAll()
                .anyRequest().authenticated()
//                .anyRequest().permitAll()
            )

//                .oauth2Login((Customizer.withDefaults()))
//             2. OAuth2 Login (BFF механізм)
            .oauth2Login(oauth2 -> oauth2
                // Тут можна додати кастомну сторінку логіну, якщо захочете пізніше
                .defaultSuccessUrl("/", true)
            )

            // 3. Загартовування сесій (Session Management)
            .sessionManagement(session -> session
                // Захист від Fixation атак: при логіні створюється нова сесія
                .sessionFixation().migrateSession()
            )

            // 4. CSRF захист (Критично для BFF)
            // Оскільки у нас є сесія і кука JSESSIONID, нам потрібен CSRF токен
            .csrf(csrf -> csrf
                // Для сучасних SPA/фронтендів використовуємо CookieCsrfTokenRepository
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            )

            // 5. OIDC Logout (Keycloak синхронізація)
            .logout(logout -> logout
                .logoutSuccessHandler(oidcLogoutSuccessHandler())
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
            );

        return http.build();
    }

    private OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
        var successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri("{baseUrl}");
        return successHandler;
    }
}