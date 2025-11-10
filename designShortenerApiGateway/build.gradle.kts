plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.design"
version = "0.0.1-SNAPSHOT"
description = "designShortenerApiGateway"

repositories {
    mavenCentral()
    // 👇 Required for Spring Cloud 2024.x (Boot 3.5 compatible)
    maven("https://repo.spring.io/milestone")
}

extra["springCloudVersion"] = "2024.0.0-M2"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // 👇 Spring Cloud Gateway starter (WebFlux-based)
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.test {
    useJUnitPlatform()
}
