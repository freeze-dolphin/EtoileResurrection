val kotlinxVersion: String by rootProject

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "2.2.10"
    id("com.ryandens.jlink-application") version "0.4.1"
    id("edu.sc.seis.launch4j") version "4.0.0"

    java
    distribution
}

group = "io.sn"

dependencies {
    implementation("com.formdev:flatlaf:3.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:${kotlinxVersion}")
    implementation(project(":core"))

    project(":core")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

launch4j {
    mainClassName = "io.sn.etoile.launch.SwingGenesisKt"
    outfile = "EtoileResurrection.Swing-${getCheckedOutGitCommitHash(7)}.exe"
    downloadUrl = "https://learn.microsoft.com/java/openjdk/download"
    bundledJrePath = "jre"
}

jlinkJre {
    modules.set(setOf("java.base", "java.desktop", "java.logging"))
}

distributions {
    main {
        distributionBaseName = "EtoileResurrection.Swing"
        version = getCheckedOutGitCommitHash(7)
        contents {
            from(
                layout.buildDirectory.dir("launch4j"),
            )
            duplicatesStrategy = DuplicatesStrategy.INCLUDE
        }
    }
}

tasks.startScripts {
    doLast {
        unixScript.delete()
        windowsScript.delete()
    }
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

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.named("createExe") {
    dependsOn("jlinkJre")

    val sourceJre = layout.buildDirectory.dir("jlink-jre/jre")
    val targetJre = layout.buildDirectory.dir("launch4j/jre")
    inputs.dir(sourceJre)
    outputs.dir(targetJre)

    doLast {
        sourceJre.get().asFile.copyRecursively(targetJre.get().asFile, overwrite = true)
    }
}

tasks.named("distTar") {
    dependsOn("createExe")
}

tasks.named("distZip") {
    dependsOn("createExe")
}
