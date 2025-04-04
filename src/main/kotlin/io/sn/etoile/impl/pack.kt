package io.sn.etoile.impl

import com.charleskorn.kaml.encodeToStream
import com.tairitsu.compose.arcaea.Chart
import io.sn.etoile.utils.*
import io.sn.etoile.utils.scenecontrol.ScenecontrolService
import io.sn.etoile.utils.scenecontrol.extractScenecontrols
import io.sn.etoile.utils.scenecontrol.loadChart
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.readBytes

class ArcpkgPackRequest(
    songlistPath: Path,
    private val song: SonglistEntry,
    private val prefix: String,
    private val packOutputPath: Path,
) {
    private val songsDir: Path = songlistPath.parent
    private val songId: String = song.id

    private val identifier = "$prefix.$songId"

    private val bgSearchDir: List<Path> = listOf(
        file(songsDir.toString(), songId).toPath(),
        file(songsDir.toString(), "..", "img", "bg").toPath(),
        file(songsDir.toString(), "..", "img", "bg", "1080").toPath()
    )

    private fun searchBgFile(bgName: String): Path? {
        if (bgName.startsWith("base_")) return null
        bgSearchDir.find {
            if (!it.exists()) return@find false
            it.listDirectoryEntries().any { subdirFile -> subdirFile.name == "$bgName.jpg" }
        }.let {
            if (it != null) {
                return Path.of(it.toString(), "$bgName.jpg")
            }
            return null
        }
    }

    private fun generateIndexEntry(): List<ImportInformationEntry> = listOf(
        ImportInformationEntry(
            directory = songId, identifier = identifier, settingsFile = "project.arcproj", version = 0, type = ArcpkgEntryType.LEVEL
        )
    )

    companion object {
        fun getDifficultyString(ratingClass: Int, rating: Int, ratingPlus: Boolean): String {
            val prefix = when (ratingClass) {
                0 -> "Past"
                1 -> "Present"
                2 -> "Future"
                3 -> "Beyond"
                4 -> "Eternal"
                else -> "Future"
            }
            if (rating <= 0) {
                return "$prefix ?"
            }
            return "$prefix $rating${if (ratingPlus) "+" else ""}"
        }

        private fun getDifficultyColor(ratingClass: Int): String = when (ratingClass) {
            0 -> "#3A6B78FF"
            1 -> "#566947FF"
            // 2 -> "#482B54FF"
            3 -> "#7C1C30FF"
            4 -> "#433455FF"
            else -> "#482B54FF"
        }

        /**
         * Logic of skinning for special songs
         */
        private fun getSkin(side: Int, songId: String, setId: String, bg: String): DifficultySkin {
            val sideStyle = when (side) {
                0 -> DifficultySkin.SideStyle.LIGHT
                1 -> DifficultySkin.SideStyle.CONFLICT
                2 -> DifficultySkin.SideStyle.COLORLESS
                3 -> DifficultySkin.SideStyle.LEPHON
                else -> DifficultySkin.SideStyle.LIGHT
            }

            val noteStyle = when (sideStyle) {
                DifficultySkin.SideStyle.LIGHT,
                DifficultySkin.SideStyle.COLORLESS,
                DifficultySkin.SideStyle.LEPHON,
                    -> DifficultySkin.NoteStyle.LIGHT

                DifficultySkin.SideStyle.CONFLICT -> DifficultySkin.NoteStyle.CONFLICT
            }

            val particleStyle = when {
                setId.startsWith("nijuusei") -> DifficultySkin.ParticleStyle.MIRAI_LIGHT
                setId.startsWith("mirai") -> {
                    when (sideStyle) {
                        DifficultySkin.SideStyle.CONFLICT -> DifficultySkin.ParticleStyle.MIRAI_CONFLICT
                        else -> DifficultySkin.ParticleStyle.MIRAI_LIGHT
                    }
                }

                bg == "lethaeus" -> DifficultySkin.ParticleStyle.MIRAI_CONFLICT

                else -> DifficultySkin.ParticleStyle.NONE
            }

            val trackStyle = when {
                songId == "tempestissimo" -> DifficultySkin.TrackStyle.TEMPESTISSIMO
                songId == "pentiment" -> DifficultySkin.TrackStyle.PENTIMENT
                songId == "arcanaeden" -> DifficultySkin.TrackStyle.ARCANA
                songId == "alexandrite" -> DifficultySkin.TrackStyle.BLACK

                side == 2 -> DifficultySkin.TrackStyle.COLORLESS
                bg.startsWith("rei") || bg == "tanoc_red" -> DifficultySkin.TrackStyle.REI
                bg.startsWith("lethaeus") || bg == "saikyostronger" -> DifficultySkin.TrackStyle.BLACK

                (setId == "nijuusei" || bg.startsWith("nijuusei")) && sideStyle == DifficultySkin.SideStyle.CONFLICT -> DifficultySkin.TrackStyle.NIJUUSEI // fix #2
                setId == "finale" && side == 1 -> DifficultySkin.TrackStyle.FINALE

                else -> DifficultySkin.TrackStyle.NONE
            }

            val accentStyle = when {
                setId == "dynamix" || songId == "alexandrite" -> DifficultySkin.AccentStyle.DYNAMIX
                else -> DifficultySkin.AccentStyle.NONE
            }

            val singleLineStyle = if (setId == "single") {
                if (songId == "neowings") DifficultySkin.SingleLineStyle.NEO
                else when (noteStyle) {
                    DifficultySkin.NoteStyle.CONFLICT -> DifficultySkin.SingleLineStyle.CONFLICT
                    else -> DifficultySkin.SingleLineStyle.LIGHT
                }
            } else DifficultySkin.SingleLineStyle.NONE

            return DifficultySkin(
                side = sideStyle,
                note = noteStyle,
                particle = particleStyle,
                accent = accentStyle,
                track = trackStyle,
                singleLine = singleLineStyle,
            )
        }

        private fun getLastOpenedChartPath(difficulties: List<DifficultyEntry>): String {
            if (difficulties.isEmpty()) throw RuntimeException("No charts exist for this song")

            return if (difficulties.any { it.ratingClass == 2 }) {
                "2.aff"
            } else {
                "${difficulties.last().ratingClass}.aff"
            }
        }

    }

    fun exec() {
        val songEntry = song

        val bg = songEntry.bg!!
        val setId = songEntry.set!!
        val side = songEntry.side!!
        val difficulties: List<DifficultyEntry> = songEntry.difficulties!!.filter { it.rating >= 0 }

        val bgToExtract = mutableMapOf<String, Path?>()

        val lastOpenedChartPath = getLastOpenedChartPath(difficulties)
        val charts = difficulties.map {
            ChartEntry(
                chartPath = "${it.ratingClass}.aff",
                audioPath = if (it.audioOverride == true) "${it.ratingClass}.ogg" else "base.ogg",
                jacketPath = if (it.jacketOverride == true) {
                    if (songsDir.resolve(songId).resolve("1080_${it.ratingClass}.jpg").exists()) {
                        "1080_${it.ratingClass}.jpg"
                    } else "${it.ratingClass}.jpg" // fix #2
                } else {
                    if (songsDir.resolve(songId).resolve("1080_base.jpg").exists()) {
                        "1080_base.jpg"
                    } else "base.jpg"
                },
                backgroundPath = when {
                    it.bg?.startsWith("base_") == true -> null
                    it.bg != null -> {
                        bgToExtract[it.bg!!] = searchBgFile(it.bg!!)
                        "${it.bg}.jpg"
                    }

                    else -> {
                        bgToExtract[bg] = searchBgFile(bg)
                        "$bg.jpg"
                    }
                },
                baseBpm = songEntry.bpmBase!!,
                bpmText = songEntry.bpmText!!,
                syncBaseBpm = false,
                title = songEntry.titleLocalized!!.en,
                composer = songEntry.artist!!,
                charter = if (prefix == "lowiro") "© Lowiro" else it.chartDesigner.let { ctr -> if (ctr.contains("\n")) "" else ctr },
                alias = if (prefix == "lowiro") it.chartDesigner else null,
                illustrator = it.jacketDesigner,
                difficulty = getDifficultyString(it.ratingClass, it.rating, it.ratingPlus == true),
                chartConstant = it.rating + if (it.ratingPlus == true) 0.7F else 0F,
                difficultyColor = getDifficultyColor(it.ratingClass),
                skin = getSkin(side, songId, setId, bg),
                previewStart = it.audioPreview ?: 0,
                previewEnd = it.audioPreviewEnd ?: 5000,
                searchTags = listOf(
                    songEntry.titleLocalized.en,
                    songEntry.titleLocalized.ja,
                    songEntry.titleLocalized.ko,
                    songEntry.titleLocalized.zhHans,
                    songEntry.titleLocalized.zhHant
                ).distinct().joinToString("\n"),
            )
        }

        // convertion of the chart
        val chartConverted = difficulties.map { it.ratingClass }.zip(difficulties.map {
            val chartFile = file(songsDir.toString(), songId, "${it.ratingClass}.aff")
            Chart.fromAff(chartFile.readText(charset = Charsets.UTF_8)).serializeForArcCreate()
        })

        // generate .sc.json
        val tgDifficulties = difficulties.map {
            loadChart(songsDir.resolve(songId).resolve("${it.ratingClass}.aff"))
        }

        val scDifficulties = tgDifficulties.map {
            extractScenecontrols(it)
        }

        val scenecontrolSerialized =
            difficulties.filterIndexed { idx, _ -> // filter charts with scenecontrols that need to be serialized
                scDifficulties[idx].isNotEmpty()
            }.let { scCharts ->
                scCharts.map { it.ratingClass }.zip(List(scCharts.size) { idx ->
                    val chartScenecontrols = scDifficulties[idx]
                    val chartTimingGroups = tgDifficulties[idx]
                    val service = ScenecontrolService(chartScenecontrols, chartTimingGroups, idx)
                    service.export()
                })
            }

        // pack all up
        val arcpkgFile = packOutputPath.resolve("$prefix.$songId.arcpkg").toFile()
        arcpkgFile.createNewFile()

        FileOutputStream(arcpkgFile).use { fos ->
            ZipOutputStream(fos).use { zos ->
                val indexEntry = generateIndexEntry()
                val projectInformation = ProjectInformation(
                    lastOpenedChartPath = lastOpenedChartPath,
                    charts = charts
                )

                zos.putNextEntry(ZipEntry("index.yml"))
                yaml.encodeToStream(indexEntry, zos)
                zos.closeEntry()

                zos.putNextEntry(ZipEntry("$songId/project.arcproj"))
                yaml.encodeToStream(projectInformation, zos)
                zos.closeEntry()

                chartConverted.forEach { (ratingClass, chartContent) ->
                    zos.putNextEntry(ZipEntry("$songId/$ratingClass.aff"))
                    zos.write(chartContent.toByteArray(charset = Charsets.UTF_8))
                    zos.closeEntry()
                }

                Files.list(songsDir.resolve(songId)).filter {
                    it.name.endsWith(".jpg") || it.name.endsWith(".ogg") || it.name.endsWith(".wav")
                }.forEach {
                    zos.putNextEntry(ZipEntry("$songId/${it.name}"))
                    zos.write(it.readBytes())
                    zos.closeEntry()
                }

                bgToExtract.forEach { (bgName, bgPath) ->
                    if (bgPath != null) {
                        zos.putNextEntry(ZipEntry("$songId/$bgName.jpg"))
                        zos.write(bgPath.readBytes())
                        zos.closeEntry()
                    } else if (!bgName.startsWith("base_")) {
                        println("WARN: $bgName not found, altered to base bg")
                    }
                }

                scenecontrolSerialized.forEach { (ratingClass, scContent) ->
                    if (scContent == null) return@forEach
                    zos.putNextEntry(ZipEntry("$songId/$ratingClass.sc.json"))
                    zos.write(scContent.toByteArray(charset = Charsets.UTF_8))
                    zos.closeEntry()
                }

                zos.close()
            }

            fos.close()
        }

        println("Packed successfully to: ${arcpkgFile.canonicalPath}")
    }

}