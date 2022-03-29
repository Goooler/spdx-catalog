plugins {
    kotlin("jvm") version "1.5.0"
    kotlin("plugin.serialization") version "1.5.0"
    `maven-publish`
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
    signing
}

description = "The SPDX license catalog including a Kotlin Wrapper for access"
group = "io.cloudflight.license.spdx"
version = "3.16.6"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        val configuration = project.configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
        val classpath = configuration.files.joinToString(" ") { it.name }
        val createdBy = "${System.getProperty("java.version")} (${System.getProperty("java.vendor")})"

        attributes(
            "Class-Path" to classpath,
            "Created-By" to createdBy,
            "Implementation-Vendor" to "Cloudflight",
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        apiVersion = "1.3"
        languageVersion = "1.4"
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://github.com/cloudflightio/spdx-catalog")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                inceptionYear.set("2022")
                organization {
                    name.set("Cloudflight")
                    url.set("https://cloudflight.io")
                }
                developers {
                    developer {
                        id.set("klu2")
                        name.set("Klaus Lehner")
                        email.set("klaus.lehner@cloudflight.io")
                    }
                }
                scm {
                    connection.set("scm:ggit@github.com:cloudflightio/spdx-catalog.git")
                    developerConnection.set("scm:git@github.com:cloudflightio/spdx-catalog.git")
                    url.set("https://github.com/cloudflightio/spdx-catalog")
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {  //only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(System.getenv("MAVEN_USERNAME"))
            password.set(System.getenv("MAVEN_PASSWORD"))
        }
    }
}

signing {
    setRequired {
        System.getenv("PGP_SECRET") != null
    }
    useInMemoryPgpKeys(System.getenv("PGP_SECRET"), System.getenv("PGP_PASSPHRASE"))
    sign(publishing.publications.getByName("maven"))
}
