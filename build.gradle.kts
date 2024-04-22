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
    kotlin("jvm") version "1.9.23"
    kotlin("kapt") version "1.9.23"
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
    val kotlinStdLib = "org.jetbrains.kotlin:kotlin-stdlib:1.9.23"
    val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:1.9.23"
    val kotlinxCoroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0"

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Pool2
    compileOnly("org.apache.commons:commons-pool2:2.12.0")
    testImplementation("org.apache.commons:commons-pool2:2.12.0")

    // Apache Logging
    compileOnly("org.apache.logging.log4j:log4j-api:2.23.1")
    testImplementation("org.apache.logging.log4j:log4j-core:2.13.3")

    // Slf4j
    compileOnly("org.slf4j:slf4j-api:2.0.12")
    testImplementation("org.slf4j:slf4j-simple:2.0.12")

    // Kotlin
    compileOnly(kotlinStdLib)
    compileOnly(kotlinReflect)
    compileOnly(kotlinxCoroutinesCore)
    testImplementation(kotlinStdLib)
    testImplementation(kotlinReflect)
    testImplementation(kotlinxCoroutinesCore)
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = projectId
            groupId = projectGroup
            version = projectVersion
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