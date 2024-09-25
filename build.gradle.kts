plugins {
    java
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("com.diffplug.spotless") version "6.22.0"
    id("my-custom-plugin") // The plugin id is derived from the class name by default
    kotlin("jvm")
}

//apply<MyCustomPlugin>()

group = "com.conduct"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

spotless {
    java {
        target("src/**/*.java")
        googleJavaFormat() // Use Google Java Format
    }
    kotlin {
        target("src/**/*.kt")
        ktlint() // Use ktlint for Kotlin files
    }
}


configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.register("hello") {
    doLast {
        println("Hello, Kotlin DSL!")
    }
}
tasks.register("hello2") {
    doLast {
        println("Hello, Kotlin DSL2!")
    }
}
tasks.register<Copy>("copyFiles") {
    from("src/main/resources")
    into("build/myresources")
}

open class CustomTask: DefaultTask() {
    @Input
    var message: String = "Default Message"

    @TaskAction
    fun printMessage() {
        println(message)
    }
}

tasks.register<CustomTask>("printCustomMessage") {
    message = "my custom message to print"
}


tasks.register("greet") {
    val greeting: String by project

    doLast {
        println("Greeting: ${greeting ?: "Hello!"}")
    }
}