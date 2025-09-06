plugins {
    id("java")
}

group = "org.read"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation(project(":demo3"))
}

tasks.test {
    useJUnitPlatform()
}