plugins {
    java
    id("maven-publish")
}

repositories {
    mavenCentral()
    mavenLocal()
}

group = "org.another"
version = "0.0.1-SNAPSHOT"
description = "AnotherBootShared"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = "AnotherBootShared"
            version = project.version.toString()
        }
    }
    repositories {
        mavenLocal()
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb:3.5.6")
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
