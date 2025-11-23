import com.google.protobuf.gradle.*
import java.net.URI

plugins {
    java
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.4"
}

group = "org.design"
version = "0.0.1-SNAPSHOT"
description = "designUrlShortenerRedirector"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    // Add the standard Spring Release repository
    maven { url = URI("https://repo.spring.io/release") }
    // Keep the milestone repository if you need dependencies from there
    maven("https://repo.spring.io/milestone")
}

extra["springCloudVersion"] = "2024.0.0-M2"

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.64.0"
        }
    }
    generateProtoTasks {
        all().forEach { task ->
            task.plugins {
                id("grpc")
            }
        }
    }
}

sourceSets {
    main {
        java {
            srcDir("build/generated/source/proto/main/java")
            srcDir("build/generated/source/proto/main/grpc")
        }
    }
}


dependencies {
    // Core Spring Boot dependencies
    // implementation("org.springframework.boot:spring-boot-starter-web")
    // implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")

    implementation("javax.annotation:javax.annotation-api:1.3.2")

    // R2DBC Driver - REMOVED explicit version for Spring Boot management to handle it
    implementation("io.r2dbc:r2dbc-postgresql:0.8.13.RELEASE")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    // External gRPC starter (Keep explicit version)
    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")

    // gRPC + Protobuf - Versions managed by Spring Boot
    implementation("io.grpc:grpc-netty-shaded")
    implementation("io.grpc:grpc-protobuf")
    implementation("io.grpc:grpc-stub")
    implementation("com.google.protobuf:protobuf-java")

    // REMOVED: implementation("javax.annotation:javax.annotation-api")
    // This is often redundant and causes conflicts in SB 3 (Jakarta namespace)

    // Spring Cloud Eureka
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("io.lettuce:lettuce-core")

    // Prometheus metrics
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}