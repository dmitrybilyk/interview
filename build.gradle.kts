import org.gradle.kotlin.dsl.*


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

//// Create a custom task to run Docker Compose up
//tasks.register<DefaultTask>("dockerComposeUp") {
//    val profile = if (project.hasProperty("profile")) project.property("profile") as String else "default"
//
//    // Construct the command line arguments based on the profile
//    val composeFiles = mutableListOf("docker-compose/docker-compose.yaml")
//    when (profile) {
//        "postgres" -> composeFiles.add("docker-compose/docker-compose-postgres.yaml")
//        "rabbit" -> composeFiles.add("docker-compose/docker-compose-rabbit.yaml")
//    }
//
//    doLast {
//        try {
//            val processBuilder = ProcessBuilder("docker-compose", "-f", *composeFiles.toTypedArray(), "up", "-d")
//            processBuilder.inheritIO()  // Inherit input/output for console visibility
//            val process = processBuilder.start()
//
//            // Capture output and error streams
//            val output = process.inputStream.bufferedReader().readText()
//            val errorOutput = process.errorStream.bufferedReader().readText()
//
//            val exitCode = process.waitFor()  // Wait for the process to complete
//
//            if (exitCode != 0) {
//                throw IOException("Failed to start Docker Compose with exit code $exitCode: $errorOutput")
//            } else {
//                println(output)  // Print the normal output
//            }
//        } catch (e: IOException) {
//            throw RuntimeException("Error executing Docker Compose up: ${e.message}", e)
//        }
//    }
//}

//// Create a custom task to run Docker Compose down
//tasks.register<DefaultTask>("dockerComposeDown") {
//    val profile = if (project.hasProperty("profile")) project.property("profile") as String else "default"
//
//    // Construct the command line arguments based on the profile
//    val composeFiles = mutableListOf("docker-compose/docker-compose.yaml")
//    when (profile) {
//        "postgres" -> composeFiles.add("docker-compose/docker-compose-postgres.yaml")
//        "rabbit" -> composeFiles.add("docker-compose/docker-compose-rabbit.yaml")
//    }
//
//    // Task action to execute Docker Compose down
//    doLast {
//        try {
//            val processBuilder = ProcessBuilder("docker-compose", "-f", *composeFiles.toTypedArray(), "down")
//            processBuilder.inheritIO()  // Inherit input/output for console visibility
//            val process = processBuilder.start()
//            val exitCode = process.waitFor()  // Wait for the process to complete
//
//            if (exitCode != 0) {
//                throw IOException("Failed to stop Docker Compose with exit code $exitCode")
//            }
//        } catch (e: IOException) {
//            throw RuntimeException("Error executing Docker Compose down: ${e.message}", e)
//        }
//    }
//}

// Configure the bootRun task to start and stop Docker Compose
//tasks.named<BootRun>("bootRun") {
//    doFirst {
//        tasks["dockerComposeUp"].actions.forEach { it.execute(this) }  // Start Docker Compose
//    }
//
////    doLast {
////        tasks["dockerComposeDown"].actions.forEach { it.execute(this) }  // Stop Docker Compose
////    }
////
////    // Ensure that 'dockerComposeDown' runs even if 'bootRun' fails
////    finalizedBy(tasks["dockerComposeDown"])
//}

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
//    if (project.hasProperty("rabbit")) {
        implementation("org.springframework.boot:spring-boot-starter-amqp")
        implementation("org.springframework.boot:spring-boot-starter-aop")
        implementation("org.springframework.boot:spring-boot-starter-data-redis")
        implementation("org.apache.cxf:cxf-spring-boot-starter-jaxws:4.0.0")
        implementation("org.springframework.ws:spring-ws-core")
        implementation("redis.clients:jedis")
//    }
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("commons-io:commons-io:2.11.0")
    runtimeOnly("org.postgresql:postgresql")
//    if (project.hasProperty("postgres")) {
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    }
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-inline:5.2.0")
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