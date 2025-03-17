package io.sn.etoile

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import io.sn.etoile.impl.ArcpkgConvertRequest
import io.sn.etoile.impl.ExportBgMode
import io.sn.etoile.impl.ExportConfiguration

class ToAceCommand : CliktCommand(name = "pack") {
    override fun run() {

    }
}

class FromAceCommand : CliktCommand(name = "export") {
    private val arcpkgs by argument().file(mustExist = true, mustBeReadable = true, canBeDir = false).multiple().unique()
    private val prefix by argument(name = "prefix", help = "The prefix of the song id")

    private val exportBgMode by option(names = arrayOf("--export-bg-mode", "--mode"), help = "").choice(
        "simplified",
        "precise",
        "overwrite",
        "auto_rename"
    ).default("auto_rename")

    private val exportSet by option(
        names = arrayOf("--pack", "-p"),
        help = "The name of the pack to export, defaults to single"
    ).default("single")
    private val exportVersion by option(
        names = arrayOf("--version", "-v"),
        help = "The version of the songs, defaults to 1.0"
    ).default("1.0")
    private val exportTime by option(
        names = arrayOf("--time", "-t"),
        help = "The time when these songs are added, defaults to current system time"
    ).default((System.currentTimeMillis() / 1000L).toString())

    override fun run() {
        ArcpkgConvertRequest(
            arcpkgs,
            prefix,
            ExportConfiguration(
                exportSet = exportSet,
                exportVersion = exportVersion,
                exportBgMode = ExportBgMode.valueOf(exportBgMode.uppercase()),
                exportTime = exportTime.toLong(),
            )
        ).exec()
    }
}

class EtoileRessurectionCommand : CliktCommand(name = "etoile") {
    override fun run() = Unit
}

fun main(args: Array<String>) {
    EtoileRessurectionCommand().subcommands(ToAceCommand(), FromAceCommand()).main(args)

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

    if (args.size < 3) {
        usage.forEach(System.out::println)
        exitProcess(0)
    }

    val arcpkgs = args.sliceArray(2 until args.size)
    val prefix = args[0]
    val mode = ExportBgMode.valueOf(args[1])
    */
}
