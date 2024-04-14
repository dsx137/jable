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
}
```


