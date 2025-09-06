plugins {
    java
    application
//    id("org.springframework.boot") version "3.5.5"
//    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.read"
version = "0.0.1-SNAPSHOT"
description = "demo"

dependencies {
    implementation("org.apache.commons:commons-lang3:3.12.0")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
}

repositories{
    mavenCentral()
}

application {
    mainClass.set("com.example.demo.DemoApplication")    // Kotlin DSL (note Kt for Kotlin main)
}

tasks.register("writeHello") {
    inputs.property("name", "wold")
    val out = layout.buildDirectory.file("hello.txt")
    outputs.file(out)
    doLast {
        out.get().asFile.writeText("Hello world")
    }
}

tasks.test {
    useJUnitPlatform()
}
//tasks.forEach { println("${it.name} - ${it.description}") }
//java {
//    toolchain {
//        languageVersion = JavaLanguageVersion.of(17)
//    }
//}
//
//repositories {
//    mavenCentral()
//}
//
//dependencies {
//    implementation("org.springframework.boot:spring-boot-starter-webflux")
//    testImplementation("org.springframework.boot:spring-boot-starter-test")
//    testImplementation("io.projectreactor:reactor-test")
//    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
//}
//
//tasks.withType<Test> {
//    useJUnitPlatform()
//}
