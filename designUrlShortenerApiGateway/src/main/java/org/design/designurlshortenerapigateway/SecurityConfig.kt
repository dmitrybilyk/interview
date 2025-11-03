//package org.design.designurlshortenerapigateway
//
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.http.HttpMethod
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
//import org.springframework.security.config.web.server.ServerHttpSecurity
//import org.springframework.security.web.server.SecurityWebFilterChain
//
//@Configuration
//@EnableWebFluxSecurity
//class SecurityConfig {
//
//    @Bean
//    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
//        return http
//            .csrf { csrf -> csrf.disable() }  // MUST disable CSRF
//            .authorizeExchange { exchanges ->
//                exchanges
//                    .pathMatchers(HttpMethod.POST, "/api/**").permitAll()
//                    .pathMatchers(HttpMethod.GET, "/**").permitAll()
//                    .anyExchange().permitAll()
//            }
//            .build()
//    }
//}