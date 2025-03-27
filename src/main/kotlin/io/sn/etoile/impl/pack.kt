package io.sn.etoile.impl

import com.charleskorn.kaml.encodeToStream
import com.tairitsu.compose.arcaea.Chart
import io.sn.etoile.*
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.*

class ArcpkgPackRequest(
    private val songlistPath: Path,
    private val songlist: List<SonglistEntry>,
    private val songId: String,
    private val prefix: String,
    private val packOutputPath: Path,
) {
    private val songsDir: Path = songlistPath.parent

    private val identifier = "$prefix.$songId"

    private val bgSearchDir: List<Path> = listOf(
        file(songsDir.toString(), songId).toPath(),
        file(songsDir.toString(), "..", "img", "bg").toPath(),
        file(songsDir.toString(), "..", "img", "bg", "1080").toPath()
    )

    private fun searchBgFile(bgName: String): Path? {
        bgSearchDir.find { it.listDirectoryEntries().any { subdirFile -> subdirFile.name == "$bgName.jpg" } }.let {
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
                DifficultySkin.SideStyle.LIGHT -> DifficultySkin.NoteStyle.LIGHT
                DifficultySkin.SideStyle.COLORLESS -> DifficultySkin.NoteStyle.LIGHT
                DifficultySkin.SideStyle.LEPHON -> DifficultySkin.NoteStyle.LIGHT
                DifficultySkin.SideStyle.CONFLICT -> DifficultySkin.NoteStyle.CONFLICT
            }

            val particleStyle = when {
                setId.startsWith("nijuusei") -> DifficultySkin.ParticleStyle.MIRAI_CONFLICT
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

                setId == "nijuusei" || bg.startsWith("nijuusei") -> DifficultySkin.TrackStyle.NIJUUSEI
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
        val songEntry = songlist.filter { it.deleted != true }.find { it.id == songId }.apply {
            if (this == null) {
                throw RuntimeException("Song not found: $songId")
            }
        }
        songEntry!!

        val bg = songEntry.bg!!
        val setId = songEntry.set!!
        val side = songEntry.side!!
        val difficulties: List<DifficultyEntry> = songEntry.difficulties!!

        val lastOpenedChartPath = getLastOpenedChartPath(difficulties)
        val charts = difficulties.map {
            ChartEntry(
                chartPath = "${it.ratingClass}.aff",
                audioPath = if (it.audioOverride == true) "${it.ratingClass}.ogg" else "base.ogg",
                jacketPath = if (it.jacketOverride == true) "${it.ratingClass}.jpg" else {
                    if (songsDir.resolve(songId).resolve("1080_base.jpg").exists()) {
                        "1080_base.jpg"
                    } else "base.jpg"
                },
                backgroundPath = if (it.bg != null) "${it.bg}.jpg" else "$bg.jpg",
                baseBpm = songEntry.bpmBase!!,
                bpmText = songEntry.bpmText!!,
                syncBaseBpm = false,
                title = songEntry.titleLocalized!!.en,
                composer = songEntry.artist!!,
                alias = it.chartDesigner.let { chartDsg ->
                    chartDsg.ifEmpty { null }
                    chartDsg
                },
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
        val scenecontrolSerialized =
            difficulties.filter { // filter charts with scenecontrols that need to be serialized
                Chart.fromAff(
                    songsDir.resolve(songId).resolve("${it.ratingClass}.aff").readText(charset = Charsets.UTF_8)
                ).let { chart ->
                    val sc = mutableListOf(*chart.mainTiming.getScenecontrols().toTypedArray())
                    if (sc.isNotEmpty()) return@let true // return in advance to be faster

                    sc.addAll(chart.subTiming.map { subTiming -> subTiming.value.getScenecontrols() }
                        .fold(listOf()) { a, b ->
                            val reduceResult = a + b
                            if (reduceResult.isNotEmpty()) return@let true // return in advance to be faster
                            reduceResult
                        })

                    // println("${it.ratingClass}.aff of $songId has ${sc.size} scenecontrols")
                    sc.isNotEmpty()
                }
            }.let { scCharts ->
                scCharts.map { it.ratingClass }.zip(scCharts.map {
                    // TODO()
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

                chartConverted.forEach {
                    zos.putNextEntry(ZipEntry("$songId/${it.first}.aff"))
                    zos.write(it.second.toByteArray(charset = Charsets.UTF_8))
                    zos.closeEntry()
                }

                Files.list(songsDir.resolve(songId)).filter {
                    it.name.endsWith(".jpg") || it.name.endsWith(".ogg") || it.name.endsWith(".wav")
                }.forEach {
                    zos.putNextEntry(ZipEntry("$songId/${it.name}"))
                    zos.write(it.readBytes())
                    zos.closeEntry()
                }

                searchBgFile(bg).let {
                    if (it != null) {
                        zos.putNextEntry(ZipEntry("$songId/$bg.jpg"))
                        zos.write(it.readBytes())
                        zos.closeEntry()
                    }
                }

                zos.close()
            }

            fos.close()
        }

        println("Packed successfully to: ${arcpkgFile.canonicalPath}")
    }

}