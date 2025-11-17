import com.google.protobuf.gradle.*

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
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("net.devh:grpc-server-spring-boot-starter:3.1.0.RELEASE")

    // gRPC + Protobuf
    implementation("io.grpc:grpc-netty-shaded:1.64.0")
    implementation("io.grpc:grpc-protobuf:1.64.0")
    implementation("io.grpc:grpc-stub:1.64.0")
    implementation("com.google.protobuf:protobuf-java:3.25.3")

    implementation("javax.annotation:javax.annotation-api:1.3.2")

    // Spring Cloud Eureka
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("io.lettuce:lettuce-core")

    // Prometheus metrics
    implementation("io.micrometer:micrometer-registry-prometheus")

    // PostgreSQL
    implementation("org.postgresql:postgresql")

    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

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
