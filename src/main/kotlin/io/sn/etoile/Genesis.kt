package io.sn.etoile

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.unique
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.float
import com.github.ajalt.clikt.parameters.types.path
import io.sn.etoile.impl.ArcpkgConvertRequest
import io.sn.etoile.impl.ArcpkgPackRequest
import io.sn.etoile.impl.ExportBgMode
import io.sn.etoile.impl.ExportConfiguration
import java.io.File
import java.nio.file.Path

class PackCommand : CliktCommand(name = "pack") {
    private val songsDir: Path by argument(name = "songsDir", help = "songs dir to be processed on").path(
        mustExist = true, mustBeReadable = true, canBeFile = false
    ).validate {
        require(file(it.toString(), "songlist").exists()) { "songlist not found in songs dir" }
    }

    private val packOutputPath by option(
        names = arrayOf("--outputDir", "-o"), help = "The path to the .arcpkg file to be packed"
    ).path(mustExist = true, canBeFile = false, canBeDir = true, mustBeWritable = true).default(File(".").toPath())

    private val prefix by option(names = arrayOf("--prefix", "-p"), help = "The prefix of the song id").required()

    private val songId: String by option(
        names = arrayOf("--songId", "--id", "-s"), help = "The identity of the song to be packed"
    ).required()

    override fun run() {
        ArcpkgPackRequest(
            songsDir = songsDir,
            songId = songId,
            prefix = prefix,
            packOutputPath = packOutputPath
        ).exec()
    }
}

class ExportCommand : CliktCommand(name = "export") {
    private val arcpkgs: Set<Path> by argument(name = "arcpkgs", help = ".arcpkg files to be processed on").path(
        mustExist = true, mustBeReadable = true, canBeDir = false
    ).multiple().unique()

    private val prefix by option(names = arrayOf("--prefix", "-p"), help = "The prefix of the song id").required()

    private val exportBgMode by option(names = arrayOf("--export-bg-mode", "--mode"), help = "Please refer to the README file").choice(
        "simplified", "precise", "overwrite", "auto_rename"
    ).default("auto_rename")

    private val exportSet by option(
        names = arrayOf("--pack", "--set", "-s"), help = "The name of the pack to export, defaults to single"
    ).default("single")
    private val exportVersion by option(
        names = arrayOf("--version", "-v"), help = "The version of the songs, defaults to 1.0"
    ).default("1.0")
    private val exportTime by option(
        names = arrayOf("--time", "-t"), help = "The time when these songs are added, defaults to current system time"
    ).default((System.currentTimeMillis() / 1000L).toString())

    private val exportOutput by option(
        names = arrayOf("--output", "-o"), help = "The output of the song output, defaults to './result'"
    ).file(mustExist = true, canBeDir = true, canBeFile = false).default(file(".", "result"))

    override fun run() {
        ArcpkgConvertRequest(
            arcpkgs, prefix, ExportConfiguration(
                exportSet = exportSet,
                exportVersion = exportVersion,
                exportTime = exportTime.toLong(),
                exportDirectory = exportOutput,
                exportBgMode = ExportBgMode.valueOf(exportBgMode.uppercase())
            )
        ).exec()
    }
}

class EtoileRessurectionCommand : CliktCommand(name = "etoile") {
    override fun run() = Unit
}

fun main(args: Array<String>) {

    /*
    val usage: Array<String> = arrayOf(
        "Etoile Resurrection",
        "USAGE: java -cp <PATH TO Aetherium.jar> io.sn.aetherium.implementations.crystals.EtoileRessurectionKt <PREFIX> <MODE> [arcpkgs]",
        "Available Modes:",
        " - SIMPLIFIED: extract backgrounds, ignore if existed",
        " - PRECISE: use tree structure",
        " - OVERWRITE: extract backgrounds, overwrite if existed",
        " - AUTO_RENAME: add prefix to filenames to avoid conflicts",
        "The convert result will be in `\$PWD/result/`"
    )
    */

    EtoileRessurectionCommand().subcommands(PackCommand(), ExportCommand()).main(args)

}
