plugins {
    kotlin("jvm") version "1.6.10"
    `maven-publish`
}

group = "io.cloudflight.license.spdx"
version = "3.16"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.3")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.3")
}

