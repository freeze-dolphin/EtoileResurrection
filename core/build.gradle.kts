val affComposeVersion: String by rootProject
val ktomlVersion: String by rootProject
val kamlVersion: String by rootProject
val kotlinxVersion: String by rootProject
val kotlinVersion: String by rootProject

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.2.10"

    java
    `java-library`
}

group = "io.sn"

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinxVersion}")
    implementation("com.akuleshov7:ktoml-core:${ktomlVersion}")
    implementation("com.akuleshov7:ktoml-file:${ktomlVersion}")
    implementation("com.charleskorn.kaml:kaml:${kamlVersion}")

    implementation("org.jetbrains.kotlin:kotlin-scripting-common")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm")
    implementation("org.jetbrains.kotlin:kotlin-scripting-jvm-host")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies")
    implementation("org.jetbrains.kotlin:kotlin-scripting-dependencies-maven")
    // coroutines dependency is required for this particular definition
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")

    implementation("com.github.freeze-dolphin:aff-compose:${affComposeVersion}")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
