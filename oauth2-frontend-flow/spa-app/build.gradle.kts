plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

description = "Static host for the vanilla-JS SPA that talks to Keycloak directly (PKCE, public client)"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
}
