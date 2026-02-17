package io.sn.etoile.launch

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.unique
import com.github.ajalt.clikt.parameters.arguments.validate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.file
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path
import com.sun.management.OperatingSystemMXBean
import io.sn.etoile.impl.*
import io.sn.etoile.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.lang.management.ManagementFactory
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.math.min
import kotlin.system.exitProcess

class PackCommand : CliktCommand(name = "pack") {

    private val cpuCores = Runtime.getRuntime().availableProcessors()
    private val totalRamGB =
        OperatingSystemMXBean::class.java.cast(ManagementFactory.getOperatingSystemMXBean()).totalMemorySize / (1024.0 * 1024 * 1024)

    private val songlistPath: Path by argument(name = "songlist", help = "The songlist file to be processed on").path(
        mustExist = true, mustBeReadable = true, canBeFile = true, canBeDir = false
    ).validate {
        require(
            try {
                json.parseToJsonElement(it.toFile().readText(Charsets.UTF_8))
                true
            } catch (_: Exception) {
                false
            }
        ) { "Invalid json format" }
    }

    private val packOutputPath by option(
        names = arrayOf("--outputDir", "-o"), help = "The output dir, defaults to './result'"
    ).path(mustExist = true, canBeFile = false, canBeDir = true, mustBeWritable = true).default(File("./result").toPath())

    private val prefix by option(names = arrayOf("--prefix", "-p"), help = "The prefix of the song id").required()

    private val songId: String by option(
        names = arrayOf("--songId", "--id", "-s"), help = "The identity of the song to be packed"
    ).required()

    private val regexMode: Boolean by option(
        names = arrayOf("--regex", "-re"),
        help = "Enable regex matching mode for songId"
    ).flag("--noregex", default = false)

    private val skipOnExist: Boolean by option(
        names = arrayOf("--skipOnExist", "-sk"),
        help = "Skip packing if target .arcpkg is already existed"
    ).flag("--overwrite", default = false)

    private val parallelJobs: Int by option(
        names = arrayOf("--jobs", "-j"),
        help = "The number of parallel jobs to run, defaults to `(min(RAM/2GB, threads)`"
    ).int().default(min(cpuCores, (totalRamGB / 2).toInt()))

    private fun packMapper(song: SonglistEntry) {
        ArcpkgPackRequest(
            songlistPath = songlistPath,
            song = song,
            prefix = prefix,
            packOutputPath = packOutputPath
        ).exec()
    }

    override fun run() {
        val songlist = json.decodeFromString<Songlist>(songlistPath.readText(charset = Charsets.UTF_8)).songs

        var songs: List<SonglistEntry> = songlist.filter {
            val idMatching = if (regexMode) {
                val regex = songId.toRegex()
                it.id.matches(regex)
            } else {
                it.id == songId
            }
            idMatching && it.deleted != true
        }

        if (songs.isEmpty()) throw RuntimeException("No song is matched with: $songId")

        songs = songs.filter {
            val targetFile = packOutputPath.resolve(ArcpkgPackRequestUtil.getIdentifier(prefix, it.id) + ".arcpkg").toFile()
            (targetFile.exists() && skipOnExist).not()
        }

        if (songs.isEmpty()) {
            println("Skipped all songs due to existence")
            exitProcess(0)
        }

        if (!regexMode && songs.size > 1) throw RuntimeException("Duplicated songs found: $songs")

        val parted = greedyPartition(songs.size, min(parallelJobs, songs.size))
        println(
            "Packing ${songs.size} song(s)\n" +
                "CPU Core: ${Runtime.getRuntime().availableProcessors()}\n" +
                "Deploying $parallelJobs job(s)\n" +
                "Partition: $parted"
        )

        val listJobs = mutableListOf(0)
        parted.forEachIndexed { index, partSize ->
            listJobs.add(listJobs[index] + partSize)
        }

        runBlocking {
            (1 until listJobs.size).forEach { jobIndex ->
                println("#$jobIndex: \t${listJobs[jobIndex - 1]}\t - \t${listJobs[jobIndex]}")
                launch(Dispatchers.IO) {
                    songs.subList(listJobs[jobIndex - 1], listJobs[jobIndex]).forEach { song ->
                        packMapper(song)
                    }
                }
            }
        }
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
        names = arrayOf("--outputDir", "-o"), help = "The output dir, defaults to './result'"
    ).file(mustExist = true, canBeDir = true, canBeFile = false).default(file(".", "result"))

    private val disableDiffEntriesCompletion by option(
        names = arrayOf("--disable-diff-entries-completion"), help = "Disable difficulty-entries auto completion for missing .aff"
    ).flag(default = false)

    override fun run() {
        ArcpkgConvertRequest(
            arcpkgs, prefix, ExportConfiguration(
                exportSet = exportSet,
                exportVersion = exportVersion,
                exportTime = exportTime.toLong(),
                exportDirectory = exportOutput,
                exportBgMode = ExportBgMode.valueOf(exportBgMode.uppercase()),
                enableDiffEntriesCompletion = !disableDiffEntriesCompletion
            )
        ).exec()
    }
}

class CombineCommand : CliktCommand(name = "combine") {
    private val arcpkgs: Set<Path> by argument(name = "arcpkgs", help = ".arcpkg files to be processed on").path(
        mustExist = true, mustBeReadable = true, canBeDir = false
    ).multiple().unique()

    private val prefix by option(names = arrayOf("--prefix", "-p"), help = "The prefix of the song id").required()

    private val songlistPath: Path by argument(name = "songlist", help = "The songlist file to be processed on").path(
        mustExist = true, mustBeReadable = true, canBeFile = true, canBeDir = false
    ).validate {
        require(
            try {
                json.parseToJsonElement(it.toFile().readText(Charsets.UTF_8))
                true
            } catch (_: Exception) {
                false
            }
        ) { "Invalid json format" }
    }

    private val packlistPath: Path by argument(name = "packlist", help = "The packlist file to be processed on").path(
        mustExist = true, mustBeReadable = true, canBeFile = true, canBeDir = false
    ).validate {
        require(
            try {
                json.parseToJsonElement(it.toFile().readText(Charsets.UTF_8))
                true
            } catch (_: Exception) {
                false
            }
        ) { "Invalid json format" }
    }

    private val combineOutput by option(
        names = arrayOf("--output", "-o"), help = "The output of the result .arcpkg, defaults to './result'"
    ).file(mustExist = true, canBeDir = true, canBeFile = false).default(file(".", "result"))

    private val packId: String by option(
        names = arrayOf("--packId", "--id", "-s"), help = "The identity of the pack to be combined"
    ).required()

    private val regexMode: Boolean by option(
        names = arrayOf("--regex", "-re"),
        help = "Enable regex matching mode for packId"
    ).flag("--no-regex", default = false)

    private val appendSingle: Boolean by option(
        names = arrayOf("--append-single", "-a"),
        help = "Append single pack information to the packlist"
    ).flag("--no-single", default = false)

    override fun run() {
        val packlist = json.decodeFromString<Packlist>(packlistPath.toFile().readText(Charsets.UTF_8)).packs
        val songlist = json.decodeFromString<Songlist>(songlistPath.readText(charset = Charsets.UTF_8)).songs

        val packs: List<PacklistEntry> = if (regexMode) {
            val regex = packId.toRegex()
            val packs = packlist.filter { it.id.matches(regex) }

            if (packs.isEmpty()) throw RuntimeException("No song is matched with: $packId")

            packs
        } else {
            val packs = packlist.filter { it.id == packId }

            if (packs.isEmpty()) throw RuntimeException("Song not found: $packId")
            if (packs.size > 1) throw RuntimeException("Duplicated songs found: $packId")

            packs
        }

        ArcpkgCombineRequest(
            packlistPath = packlistPath,
            arcpkgs = arcpkgs,
            songlist = songlist,
            packlist = packs,
            prefix = prefix,
            appendSingle = appendSingle,
            outputFile = combineOutput
        ).exec()
    }

}

class ConvertCommand : CliktCommand(name = "convert") {
    private val convertInput by argument(
        name = "source", help = "The chart file to be converted"
    ).file(mustExist = true, canBeDir = false, canBeFile = true)

    private val convertInputType by option(
        names = arrayOf("--type", "-t"),
        help = "The type of the chart file to be converted\u0085(aff = Arcaea File Format; acf = ArcCreate File Format)"
    ).choice("aff", "acf").required()

    private val convertOutput by option(
        names = arrayOf("--output", "-o"), help = "The relative path to where the result chart file to be placed, defaults to '(sourceFileName).convert.aff'"
    ).file(mustExist = false, canBeDir = false, canBeFile = true)

    override fun run() {
        val type = when (convertInputType) {
            "aff" -> ChartType.Arcaea
            "acf" -> ChartType.ArcCreate
            else -> {
                throw IllegalArgumentException("invalid chart type: $convertInputType")
            }
        }

        ChartConvertRequest(type, convertInput, convertOutput).exec()
    }

}

class EtoileRessurectionCommand : CliktCommand(name = "EtoileResurrection") {
    override fun run() = Unit
}

fun main(args: Array<String>) {
    /*
     * legacy help message
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
    EtoileRessurectionCommand().subcommands(PackCommand(), ExportCommand(), CombineCommand(), ConvertCommand()).main(args)
}
