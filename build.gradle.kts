val affComposeVersion: String = "56b6d49a62"
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

fun getCheckedOutGitCommitHash(): String { // https://gist.github.com/JonasGroeger/7620911
    val gitFolder = "${project.projectDir}/.git/"
    val takeFromHash = 12

    /*
     * '.git/HEAD' contains either
     *      in case of detached head: the currently checked out commit hash
     *      otherwise: a reference to a file containing the current commit hash
     */
    val head = File(gitFolder + "HEAD").readText().split(":") // .git/HEAD
    val isCommit = head.size == 1 // e5a7c79edabbf7dd39888442df081b1c9d8e88fd
    // val isRef = head.size > 1     // ref: refs/heads/master

    if (isCommit) return head[0].trim().take(takeFromHash) // e5a7c79edabb

    val refHead = File(gitFolder + head[1].trim()) // .git/refs/heads/master
    return refHead.readText().trim().take(takeFromHash)
}

distributions {
    main {
        version = getCheckedOutGitCommitHash().substring(0 until 7)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
