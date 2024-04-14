# Jable

```gradle kotlin
repositories {
    maven("jitpack") {
        url = uri("https://jitpack.io")
    }
}
dependencies {
    implementation("com.github.dsx137:jable:main-SNAPSHOT")

    // The following are the options 
    // implementation the dependency if the module you are using requires it

    // Pool2
    implementation("org.apache.commons:commons-pool2:2.12.0")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
}
```


