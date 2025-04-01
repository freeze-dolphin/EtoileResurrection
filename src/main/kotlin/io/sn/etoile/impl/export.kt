package io.sn.etoile.impl

import com.charleskorn.kaml.YamlList
import com.charleskorn.kaml.yamlMap
import com.charleskorn.kaml.yamlScalar
import com.tairitsu.compose.arcaea.Chart
import com.tairitsu.compose.arcaea.LocalizedString
import io.sn.etoile.utils.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import javax.imageio.ImageIO
import kotlin.properties.Delegates

enum class ExportBgMode {
    SIMPLIFIED, PRECISE, OVERWRITE, AUTO_RENAME;
}

data class ExportConfiguration(
    val exportSet: String,
    val exportVersion: String,
    val exportBgMode: ExportBgMode,
    val exportDirectory: File,
    val exportTime: Long = getCurrentSystemTime(),
)

class ArcpkgConvertRequest(
    private val arcpkgs: Set<Path>,
    private val identifierPrefix: String,
    private val exportConfiguration: ExportConfiguration
) {

    companion object {
        fun readFileFromZip(zipFile: ZipFile, fileName: String): String {
            val entry: ZipEntry = zipFile.getEntry(fileName)
            val content = StringBuilder()

            zipFile.getInputStream(entry).use { inputStream ->
                BufferedReader(InputStreamReader(inputStream, charset("utf-8"))).use { reader ->
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        content.append(line + System.lineSeparator())
                    }
                }
            }

            return content.toString()
        }

        fun extractFileFromZipPrecise(
            zipFile: ZipFile,
            fileName: String,
            destDir: File,
            overrideFileName: String? = null,
            resizeToBg1080p: Boolean = false
        ): Int {
            val zipEntry = zipFile.getEntry(fileName)

            if (zipEntry != null) {
                val exportFileName = overrideFileName ?: unifyBgName(File(fileName).name)
                val outputFile = File(destDir, exportFileName)
                if (!outputFile.parentFile.exists()) outputFile.parentFile.mkdirs()
                extractFileFromZip(zipFile, zipEntry, fileName, outputFile, resizeToBg1080p)
                return 0
            } else {
                return 1
            }
        }

        fun extractFileFromZipSimplified(
            zipFile: ZipFile,
            fileName: String,
            destDir: File,
            overrideFileName: String? = null,
            resizeToBg1080p: Boolean = false
        ): Int {
            val zipEntry = zipFile.getEntry(fileName)
            var rt = 0

            if (zipEntry != null) {
                val exportFileName = overrideFileName ?: unifyBgName(File(fileName).name)
                val outputFile = File(destDir, exportFileName)
                if (outputFile.exists()) {
                    rt = 2
                } else {
                    extractFileFromZip(zipFile, zipEntry, fileName, outputFile, resizeToBg1080p)
                }
            } else {
                rt = 1
            }
            return rt
        }

        fun extractFileFromZipOverwrite(
            zipFile: ZipFile,
            fileName: String,
            destDir: File,
            overrideFileName: String? = null,
            resizeToBg1080p: Boolean = false
        ): Int {
            val zipEntry = zipFile.getEntry(fileName)
            var rt = 0

            if (zipEntry != null) {
                val exportFileName = overrideFileName ?: unifyBgName(File(fileName).name)
                val outputFile = File(destDir, exportFileName)
                if (outputFile.exists()) {
                    rt = 2
                }
                extractFileFromZip(zipFile, zipEntry, fileName, outputFile, resizeToBg1080p)
            } else {
                rt = 1
            }
            return rt
        }

        fun extractFileFromZipAutoRename(
            zipFile: ZipFile,
            fileName: String,
            destDir: File,
            identifier: String,
            resizeToBg1080p: Boolean = false
        ): Pair<Int, String> {
            val renamed = unifyBgName(identifier + "_" + File(fileName).name)
            return extractFileFromZipOverwrite(zipFile, fileName, destDir, renamed, resizeToBg1080p) to renamed
        }

        private fun bufferedImageToFile(bufImg: BufferedImage, overlay: BufferedImage, outputFile: File, output: FileOutputStream) {
            bufImg.createGraphics().apply {
                drawImage(overlay, 0, 0, Color.WHITE, null)
                dispose()
            }

            if (!ImageIO.write(bufImg, "jpg", output)) {
                throw RuntimeException("Unable to find a writer for file: ${outputFile.path}")
            }
        }

        private fun extractFileFromZip(
            zipFile: ZipFile,
            zipEntry: ZipEntry,
            fileName: String,
            outputFile: File,
            resizeToBg1080p: Boolean = false
        ) {
            zipFile.getInputStream(zipEntry).use { input ->
                FileOutputStream(outputFile).use { output ->
                    if (resizeToBg1080p) {
                        val img = ImageIO.read(input)
                        val width = 1920
                        val height = 1440

                        val newBufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
                        val resizedImg = resizeImage(img, 1920, 1440)

                        bufferedImageToFile(newBufferedImage, resizedImg, outputFile, output)
                    } else if (fileName.endsWith(".png") && outputFile.name.endsWith(".jpg")) {
                        val img = ImageIO.read(input)
                        val width = img.width
                        val height = img.height

                        val newBufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

                        bufferedImageToFile(newBufferedImage, img, outputFile, output)
                    } else {
                        input.copyTo(output)
                        return
                    }
                }
            }
        }

        private val ratingClassRegex = Regex("""\b\d\b""")

        fun matchRatingClass(difficultyText: String): MatchResult? {
            return ratingClassRegex.find(difficultyText)
        }

        fun unifyBgName(raw: String): String {
            return raw.replace('(', '_')
                .replace(' ', '_')
                .replace(')', '_')
                .replace('\'', '_')
                .replace(".png", ".jpg")
                .lowercase()
        }

        private val unityRichTextTags = listOf(
            "align", "allcaps", "alpha", "b", "br", "color", "cspace", "font", "font-weight", "gradient", "i",
            "indent", "line-height", "line-indent", "link", "lowercase", "margin", "mark", "mspace", "nobr",
            "noparse", "page", "pos", "rotate", "s", "size", "smallcaps", "space", "sprite", "strikethrough",
            "style", "sub", "sup", "u", "uppercase", "voffset", "width"
        )

        fun removeUnityRichTextTags(input: String): String {
            var rst = input
            unityRichTextTags.forEach { tag ->
                rst = rst.replace("</?$tag=[^>]+>|</$tag>|<$tag>".toRegex(), "")
            }
            return rst
        }

        fun resizeImage(originalImage: BufferedImage, targetWidth: Int, targetHeight: Int): BufferedImage {
            val resultingImage: Image = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH)
            val outputImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
            val g = outputImage.graphics
            g.drawImage(resultingImage, 0, 0, null)
            g.dispose()
            return outputImage
        }

    }

    private val exportDirBg = file(exportConfiguration.exportDirectory.path, "img", "bg")
    private val exportDirSongs = file(exportConfiguration.exportDirectory.path, "songs")
    private val exportDirPack = file(exportDirSongs.path, "pack")

    private val exportSonglist = file(exportDirSongs.path, "songlist")
    private val exportPacklist = file(exportDirSongs.path, "packlist")

    private fun createDirs(): Boolean {
        if (!exportDirBg.exists()) {
            exportDirBg.mkdirs().let {
                if (!it) return false
            }
        }
        if (!exportDirSongs.exists()) {
            exportDirSongs.mkdirs().let {
                if (!it) return false
            }
        }
        if (!exportDirPack.exists()) {
            exportDirPack.mkdirs().let {
                if (!it) return false
            }
        }

        return true
    }

    private fun processSongs(
        logPrefix: String,
        index: List<ImportInformationEntry>,
        arcpkgZipFile: ZipFile,
        songlist: MutableList<SonglistEntry>,
        set: String
    ) {
        index.forEachIndexed { procIdx, entry ->
            val procTextRaw = "[${procIdx + 1}/${index.size}]"
            val procText = logPrefix + procTextRaw
            val procIndent = logPrefix + " ".repeat(procTextRaw.length)
            val settings = readFileFromZip(
                arcpkgZipFile,
                "${entry.directory}/${entry.settingsFile}"
            ).let { yaml.decodeFromString<ProjectInformation>(it) }

            val charts = settings.charts

            val difficulties = mutableListOf<DifficultyEntry>()
            lateinit var baseDifficulty: DifficultyEntry

            val id = entry.identifier.removePrefix(identifierPrefix)
            val idName = "[id=${entry.identifier}]"

            println("$procText Processing: ${entry.directory} $idName")

            charts.forEach { chart ->
                val difficultyText = chart.difficulty

                val ratingClassRaw = chart.chartPath
                var ratingClassDigit: Int? = null
                var ratingClass by Delegates.notNull<Int>()
                if (ratingClassRaw.endsWith(".aff") && ratingClassRaw.removeSuffix(".aff").toIntOrNull()
                        .also { ratingClassDigit = it } != null && (0..4).contains(ratingClassDigit!!)
                ) {
                    ratingClass = ratingClassDigit!!
                } else {
                    ratingClass = when {
                        difficultyText.startsWith("Eternal") -> 4
                        difficultyText.startsWith("Beyond") -> 3
                        difficultyText.startsWith("Future") -> 2
                        difficultyText.startsWith("Present") -> 1
                        difficultyText.startsWith("Past") -> 0

                        else -> throw RuntimeException("Unable to detect ratingClass $idName")
                    }
                }
                val audioPath = chart.audioPath
                val jacketPath = chart.jacketPath
                val baseBpm = chart.baseBpm
                val bpmText = chart.bpmText
                val title = LocalizedString(removeUnityRichTextTags(chart.title))
                val artist = removeUnityRichTextTags(chart.composer)
                val charter = removeUnityRichTextTags(chart.alias ?: "")
                val jacketDesigner = removeUnityRichTextTags(chart.illustrator ?: "")

                var rating by Delegates.notNull<Int>()
                var ratingPlus by Delegates.notNull<Boolean>()
                if (chart.chartConstant != null) {
                    val constant = chart.chartConstant.toDouble()
                    rating = constant.toInt()
                    ratingPlus = constant - rating >= 0.7
                } else {
                    val matchRst = matchRatingClass(difficultyText)
                        ?: throw RuntimeException("Invalid difficulty information for ${entry.directory}/${entry.settingsFile} $idName")
                    rating = matchRst.value.toInt()
                    ratingPlus = difficultyText.endsWith("+")
                }

                lateinit var sideString: String

                val skin = chart.skin
                val side = if (skin == null) {
                    sideString = "light"
                    0
                } else {
                    when (skin.side) {
                        DifficultySkin.SideStyle.LIGHT -> {
                            sideString = "light"
                            0
                        }

                        DifficultySkin.SideStyle.CONFLICT -> {
                            sideString = "conflict"
                            1
                        }

                        DifficultySkin.SideStyle.COLORLESS -> {
                            sideString = "colorless"
                            2
                        }

                        else -> {
                            println("$procIndent Unable to parse side: ${skin.side}, defaulting to 'light'")
                            sideString = "light"
                            0
                        }

                    }
                }

                var extractBg = true

                val bgRaw: String? = chart.backgroundPath
                var bg: String
                val bgBundled = when {
                    sideString == "colorless" -> "epilogue"
                    else -> "base_$sideString"
                }

                if (bgRaw == null) {
                    extractBg = false
                    bg = bgBundled
                } else {
                    bg = unifyBgName(bgRaw.let {
                        if (it.endsWith(".jpg")) {
                            it.removeSuffix(".jpg")
                        } else if (it.endsWith(".png")) {
                            it.removeSuffix(".png")
                        } else {
                            throw RuntimeException("Invalid background image: $it $idName")
                        }
                    })
                }

                val audioPreview: Long = chart.previewStart ?: 0
                val audioPreviewEnd: Long = chart.previewEnd ?: 5000

                val difficultyEntry = DifficultyEntry(
                    _audioPath = audioPath,
                    _jacketPath = jacketPath,
                    _chartPath = ratingClassRaw,

                    ratingClass = ratingClass,
                    chartDesigner = charter,
                    jacketDesigner = jacketDesigner,
                    rating = rating,
                )

                if (extractBg) {
                    val bgPath = "${entry.directory}/$bgRaw"
                    println("$procIndent Extracting bg: $bgPath")

                    when (exportConfiguration.exportBgMode) {
                        ExportBgMode.SIMPLIFIED -> {
                            val rt = extractFileFromZipSimplified(arcpkgZipFile, bgPath, exportDirBg, null, true)
                            when (rt) {
                                2 -> println(
                                    "$procIndent | Already exists: ${
                                        file(
                                            exportDirBg.path,
                                            File(bgPath).name
                                        ).path
                                    }, ignoring..."
                                )

                                1 -> {
                                    println("$procIndent | Unable to extract: $bgPath, alter to use bundled bg: $bgBundled")
                                    bg = bgBundled
                                }
                            }
                        }

                        ExportBgMode.PRECISE -> {
                            if (extractFileFromZipPrecise(arcpkgZipFile, bgPath, exportDirBg, null, true) != 0) {
                                println("$procIndent | Unable to extract: $bgPath, alter to use bundled bg: $bgBundled")
                                bg = bgBundled
                            }
                        }

                        ExportBgMode.OVERWRITE -> {
                            val rt = extractFileFromZipOverwrite(arcpkgZipFile, bgPath, exportDirBg, null, true)
                            when (rt) {
                                2 -> println(
                                    "$procIndent | Already exists: ${
                                        file(
                                            exportDirBg.path,
                                            File(bgPath).name
                                        ).path
                                    }, overwriting..."
                                )

                                1 -> {
                                    println("$procIndent | Unable to extract: $bgPath, alter to use bundled bg: $bgBundled")
                                    bg = bgBundled
                                }
                            }
                        }

                        ExportBgMode.AUTO_RENAME -> {
                            val (rt, bgQualified) = extractFileFromZipAutoRename(
                                arcpkgZipFile,
                                bgPath,
                                exportDirBg,
                                entry.identifier,
                                true
                            )
                            when (rt) {
                                2 -> {
                                    println(
                                        "$procIndent | Already exists: ${
                                            file(
                                                exportDirBg.path,
                                                File(bgPath).name
                                            ).path
                                        }, overwriting..."
                                    )
                                    bg = bgQualified
                                }

                                1 -> {
                                    println("$procIndent | Unable to extract: $bgPath, alter to use bundled bg: $bgBundled")
                                    bg = bgBundled
                                }

                                0 -> {
                                    bg = bgQualified
                                }

                            }
                        }
                    }
                } else {
                    println("$procIndent Using bundled bg: $bg")
                }

                if (bg != bgBundled) bg = bg.removeSuffix(".jpg")

                if (ratingPlus) difficultyEntry.ratingPlus = true

                if (difficulties.size == 0) {
                    baseDifficulty = difficultyEntry.copy(
                        titleLocalized = title,
                        artist = artist,
                        bpmText = bpmText,
                        bpmBase = baseBpm,
                        side = side,
                        audioPreview = audioPreview,
                        audioPreviewEnd = audioPreviewEnd,
                        bg = bg
                    )
                } else {
                    if (title != baseDifficulty.titleLocalized) difficultyEntry.titleLocalized = title
                    if (artist != baseDifficulty.artist) difficultyEntry.artist = artist
                    if (bpmText != baseDifficulty.bpmText) difficultyEntry.bpmText = bpmText
                    if (baseBpm != baseDifficulty.bpmBase) difficultyEntry.bpmBase = baseBpm
                    if (side != baseDifficulty.side) difficultyEntry.side = side
                    if (audioPath != baseDifficulty._audioPath) difficultyEntry.audioOverride = true
                    if (jacketPath != baseDifficulty._jacketPath) difficultyEntry.jacketOverride = true
                    if (audioPreview != baseDifficulty.audioPreview) difficultyEntry.audioPreview = audioPreview
                    if (audioPreviewEnd != baseDifficulty.audioPreviewEnd) difficultyEntry.audioPreviewEnd = audioPreviewEnd
                    if (bg != baseDifficulty.bg) difficultyEntry.bg = bg
                }

                difficulties.add(difficultyEntry)
            }

            val songDir = File(exportDirSongs, id)
            if (!songDir.exists()) songDir.mkdirs()

            extractFileFromZipOverwrite(arcpkgZipFile, "${entry.directory}/${baseDifficulty._audioPath}", songDir, "base.ogg")
            extractFileFromZipOverwrite(arcpkgZipFile, "${entry.directory}/${baseDifficulty._jacketPath}", songDir, "base.jpg")

            val fileBase = File(songDir, "base.jpg")
            val origImg = ImageIO.read(fileBase)
            var is1080 = false
            val (resized256, resizedBase) = if (origImg.width >= 768) {
                is1080 = true
                resizeImage(origImg, 384, 384) to resizeImage(origImg, 768, 768)
            } else {
                resizeImage(origImg, 256, 256) to resizeImage(origImg, 512, 512)
            }

            val imgPrefix = if (is1080) "1080_" else ""

            ImageIO.write(resized256, "jpg", File(songDir, "${imgPrefix}base_256.jpg"))
            if (is1080) {
                ImageIO.write(resizedBase, "jpg", File(songDir, "${imgPrefix}base.jpg"))
                fileBase.delete()
            }

            difficulties.forEach { diffEntry ->
                val aff = readFileFromZip(arcpkgZipFile, "${entry.directory}/${diffEntry._chartPath!!}")
                val convertion = Chart.fromAcf(aff)

                if (convertion.second.ignoredTimingGroupEffects.isNotEmpty() || convertion.second.ignoredScenecontrols.isNotEmpty()) {
                    println("$procIndent Ignoring ${convertion.second.ignoredTimingGroupEffects.size}x timingGroup effects, and ${convertion.second.ignoredScenecontrols.size}x scenecontrols")
                }
                File(songDir, "${diffEntry.ratingClass}.aff").writeText(convertion.first.serializeForArcaea())
            }

            (0..2).forEach { ratingClass ->
                if (difficulties.none {
                        it.ratingClass == ratingClass
                    }) {
                    difficulties.add(
                        DifficultyEntry(
                            ratingClass = ratingClass,
                            chartDesigner = "",
                            jacketDesigner = "",
                            rating = -1
                        )
                    )
                }
            }

            difficulties.sortBy {
                it.ratingClass
            }

            songlist.add(
                SonglistEntry(
                    id = id,
                    titleLocalized = baseDifficulty.titleLocalized!!,
                    artist = baseDifficulty.artist!!,
                    bpmText = baseDifficulty.bpmText!!,
                    bpmBase = baseDifficulty.bpmBase!!,
                    set = set,
                    purchase = "",
                    audioPreview = baseDifficulty.audioPreview!!,
                    audioPreviewEnd = baseDifficulty.audioPreviewEnd!!,
                    side = baseDifficulty.side!!,
                    bg = baseDifficulty.bg!!,
                    date = exportConfiguration.exportTime,
                    version = exportConfiguration.exportVersion,
                    difficulties = difficulties,
                    deleted = null
                )
            )
        }
    }

    fun exec() {
        if (!createDirs()) {
            throw RuntimeException("Failed to create necessary directories.")
        }

        val songlist = mutableListOf<SonglistEntry>()
        val packlist = mutableListOf<PacklistEntry>()

        arcpkgs.forEach { arcpkgFile ->
            val zipFile = ZipFile(arcpkgFile.toFile())
            val index =
                readFileFromZip(zipFile, "index.yml").let { yaml.decodeFromString(ListSerializer(ImportInformationEntry.serializer()), it) }

            index.filter { entry ->
                entry.type == ArcpkgEntryType.PACK
            }.let { packEntries ->
                if (packEntries.isEmpty()) {
                    // alter to use `exportSet`
                    processSongs("", index, zipFile, songlist, exportConfiguration.exportSet)
                } else {
                    // batch process for each pack
                    packEntries.forEachIndexed { procIdx, packEntry ->
                        val procText = "{${procIdx + 1}/${packEntries.size}}"
                        val procIndent = " ".repeat(procText.length)
                        val settings = readFileFromZip(
                            zipFile,
                            "${packEntry.directory}/${packEntry.settingsFile}"
                        ).let { yaml.parseToYamlNode(it) }

                        println("$procText Processing pack: ${packEntry.directory} [id=${packEntry.identifier}]")

                        val packCover = settings content "imagePath"
                        val songIds = settings.yamlMap.get<YamlList>("levelIdentifiers")!!.items.map { node ->
                            node.yamlScalar.content
                        }
                        val packName = settings content "packName"
                        val packId = packEntry.identifier.lowercase().replace('.', '_')

                        extractFileFromZipSimplified(zipFile, "${packEntry.directory}/$packCover", exportDirPack, "select_$packId.png")

                        packlist.add(
                            PacklistEntry.fromDefaultPacklistEntry(
                                id = packId,
                                nameLocalized = LocalizedString(packName),
                                descriptionLocalized = LocalizedString("")
                            )
                        )
                        processSongs(
                            "$procIndent ",
                            index.filter { entry ->
                                songIds.contains(entry.identifier)
                            }, zipFile, songlist, packId
                        )
                    }
                }
            }
        }

        json.encodeToString(Songlist.serializer(), Songlist(songlist)).let {
            exportSonglist.writeText(it)
        }

        if (packlist.isNotEmpty()) json.encodeToString(Packlist.serializer(), Packlist(packlist)).let {
            exportPacklist.writeText(it)
        }
    }

}