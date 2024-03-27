val projectGroup: String by project
val projectVersion: String by project
val projectName: String by project

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
}

group = projectGroup
version = projectVersion

repositories {
    maven()
    {
        url = uri("https://maven.aliyun.com/repository/public/")
    }
    maven() {
        url = uri("https://maven.aliyun.com/repository/spring/")
    }
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Pool2
    compileOnly("org.apache.commons:commons-pool2:2.12.0")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
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