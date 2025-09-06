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
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Copy>("copyProps") {
    rename("application.properties", "dmytro.yml")
    from("src/main/resources")
    into(layout.buildDirectory.dir("result"))
    include("application*")
}

tasks.named<ProcessResources>("processResources") {
    filesMatching("application.properties") {
        expand(
            "version" to project.version,
            "group" to project.group,
//            "someValue" to project.findProperty("someValue") ?: "defaultValue"
        )
    }
}

tasks.named("build") {
    dependsOn("copyProps")
}