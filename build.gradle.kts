plugins {
    `java-library`
    `maven-publish`
}

group = "io.cloudflight.license.spdx"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

dependencies {
}

