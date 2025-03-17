val affComposeVersion: String = "11ab9d8e92"
val kotlinVersion: String by project
val ktorVersion: String by project
val ktomlVersion: String by project
val kamlVersion: String by project
val logbackVersion: String by project
val xmlutilVersion: String by project

plugins {
    kotlin("jvm") version "2.0.0-RC1"
    kotlin("plugin.serialization") version "1.9.23"

    java
    application
}

group = "io.sn"

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.akuleshov7:ktoml-core:${ktomlVersion}")
    implementation("com.akuleshov7:ktoml-file:${ktomlVersion}")
    implementation("com.charleskorn.kaml:kaml:${kamlVersion}")
    implementation("com.github.ajalt.clikt:clikt:5.0.3")

    implementation("com.github.freeze-dolphin:aff-compose:${affComposeVersion}")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass.set("io.sn.etoile.GenesisKt")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
