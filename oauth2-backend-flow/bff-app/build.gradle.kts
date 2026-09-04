plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

description = "Hand-rolled backend-controlled (BFF) authorization_code flow"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
