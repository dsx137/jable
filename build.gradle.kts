val projectGroup: String by project
val projectVersion: String by project
val projectName: String by project
val projectId: String by project
val projectRepository: String by project
val projectLicense: String by project

plugins {
    java
    `maven-publish`
    eclipse
    idea
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.9.22"
    kotlin("kapt") version "1.9.22"
}

group = projectGroup
version = projectVersion

repositories {
    maven {
        url = uri("https://maven.aliyun.com/repository/public/")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/spring/")
    }
    maven("jitpack") {
        url = uri("https://jitpack.io")
    }
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Pool2
    compileOnly("org.apache.commons:commons-pool2:2.12.0")

    // Kotlin
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
    compileOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to projectName,
            "Implementation-Version" to projectVersion
        )
    }
}

tasks.shadowJar {
    minimize()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = projectId
            pom {
                name.set(projectName)
                url.set(projectRepository)
                licenses {
                    license {
                        name.set(projectLicense)
                        url.set(projectRepository)
                    }
                }
            }
        }
    }
}