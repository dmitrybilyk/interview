plugins {
    java
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
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

//class MyCustomPlugin : Plugin<Project> {
//    override fun apply(target: Project) {
//        project.task("customTask") {
//            group = "custom"
//            description = "This is a custom task"
//            doLast {
//                println("Hello from custom task!")
//            }
//        }
//    }
//}
