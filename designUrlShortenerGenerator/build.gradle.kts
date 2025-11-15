plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.design"
version = "0.0.1-SNAPSHOT"
description = "designUrlShortenerGenerator"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

extra["springCloudVersion"] = "2024.0.0-M2"

repositories {
    mavenCentral()
    maven("https://repo.spring.io/milestone") // ✅ needed for Spring Cloud 2024.x
}


dependencies {
    // Core Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
//    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // Database
    implementation("org.postgresql:postgresql")

    // Metrics
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Optional: distributed coordination (Zookeeper/Curator)
    implementation("org.apache.curator:curator-framework:5.6.0")
    implementation("org.apache.curator:curator-client:5.6.0")
    implementation("org.apache.curator:curator-recipes:5.6.0")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
