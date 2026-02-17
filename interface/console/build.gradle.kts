val kotlinxVersion: String by rootProject

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.2.10"

    java
    application
}

group = "io.sn"

dependencies {
    implementation("com.github.ajalt.clikt:clikt:5.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinxVersion}")

    implementation(project(":core"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

application {
    mainClass.set("io.sn.etoile.launch.ConsoleGenesisKt")
}

fun getCheckedOutGitCommitHash(takeFromHash: Int = 12): String { // https://gist.github.com/JonasGroeger/7620911
    val gitFolder = "${rootProject.projectDir}/.git/"

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
        distributionBaseName = "EtoileResurrection.Console-universal"
        version = getCheckedOutGitCommitHash(7)
    }
}

tasks.startScripts {
    applicationName = "EtoileResurrection"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
