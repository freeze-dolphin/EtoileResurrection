package io.sn.etoile.impl

import com.charleskorn.kaml.decodeFromStream
import com.charleskorn.kaml.encodeToStream
import com.tairitsu.compose.arcaea.LocalizedString
import io.sn.etoile.utils.ArcpkgEntryType
import io.sn.etoile.utils.ImportInformationEntry
import io.sn.etoile.utils.PackInformation
import io.sn.etoile.utils.PacklistEntry
import io.sn.etoile.utils.SonglistEntry
import io.sn.etoile.utils.file
import io.sn.etoile.utils.yaml
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.io.PrintStream
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.readBytes

class ArcpkgCombineRequest(
    packlistPath: Path,
    val arcpkgs: Set<Path>,
    val songlist: List<SonglistEntry>,
    val packlist: List<PacklistEntry>,
    val prefix: String?,
    val appendSingle: Boolean,
    val outputFile: File,
) {
    private val songsDir: Path = packlistPath.parent

    private val packCoverSearchDir: List<Path> = listOf(
        file(songsDir.toString(), "pack").toPath(),
        file(songsDir.toString(), "pack", "1080").toPath(),
    )

    private fun searchPackCoverFileFromId(packId: String): Path? =
        searchPackCoverFile("1080_select_$packId.png") ?: searchPackCoverFile("select_$packId.png")

    private fun searchPackCoverFile(coverName: String): Path? {
        packCoverSearchDir.find {
            if (!it.exists()) return@find false
            it.listDirectoryEntries().any { subdirFile -> subdirFile.name == coverName }
        }.let {
            if (it != null) {
                return Path.of(it.toString(), coverName)
            }
            return null
        }
    }

    fun generateIndexEntry(packId: String, includedSongs: Array<ImportInformationEntry>): List<ImportInformationEntry> =
        listOf(*includedSongs, ImportInformationEntry(".pack", packId, "pack.yml", type = ArcpkgEntryType.PACK))

    fun exec(output: OutputStream = System.out) {
        val s = PrintStream(output)

        if (!outputFile.exists()) outputFile.mkdirs()
        if (!outputFile.isDirectory) throw RuntimeException("Output must be a directory")

        val singleAppended = mutableListOf<PacklistEntry>()
        singleAppended.addAll(packlist)
        singleAppended.add(PacklistEntry.fromDefaultPacklistEntry("single", LocalizedString("Single"), LocalizedString("")))

        (if (appendSingle) singleAppended else packlist).filter { it.packParent == null }.forEach { pack ->
            val arcpkgFile = outputFile.resolve("${pack.id}.arcpkg")
            arcpkgFile.createNewFile()

            val includedSongs = arcpkgs.map {
                val zipFile = ZipFile(it.toFile())
                val ins = zipFile.getInputStream(zipFile.getEntry("index.yml"))
                yaml.decodeFromStream<List<ImportInformationEntry>>(ins)[0] to it
            }.filter { (importInfo, path) ->
                val identifier = importInfo.identifier.removePrefix(prefix ?: "").removePrefix(".")
                val matchedSong = songlist.firstOrNull { it.id == identifier }
                if (matchedSong == null) throw RuntimeException("Song $identifier not found in provided songlist")
                matchedSong.set == pack.id || packlist.firstOrNull { it.id == matchedSong.set }?.packParent == pack.id
            }.toTypedArray()

            FileOutputStream(arcpkgFile).use { fos ->
                ZipOutputStream(fos).use { zos ->
                    val indexEntry = generateIndexEntry(pack.id, includedSongs.map { it.first }.toTypedArray())
                    zos.putNextEntry(ZipEntry("index.yml"))
                    yaml.encodeToStream(indexEntry, zos)
                    zos.closeEntry()

                    zos.putNextEntry(ZipEntry(".pack/pack.yml"))
                    yaml.encodeToStream(
                        PackInformation("pack.png", includedSongs.map { it.first.identifier }, pack.nameLocalized.en),
                        zos
                    )
                    zos.closeEntry()

                    val coverFile = searchPackCoverFileFromId(pack.id)
                    if (coverFile != null) {
                        zos.putNextEntry(ZipEntry(".pack/pack.png"))
                        zos.write(coverFile.readBytes())
                        zos.closeEntry()
                    }

                    for ((importInfo, path) in includedSongs) {
                        val zipFile = ZipFile(path.toFile())
                        zipFile.entries().toList().filter { it.name.startsWith(importInfo.directory) }.forEach { entry ->
                            zipFile.getInputStream(entry).readBytes().let {
                                zos.putNextEntry(ZipEntry(entry.name))
                                zos.write(it)
                                zos.closeEntry()
                            }
                        }
                    }
                }
            }

            s.println("Combined ${includedSongs.size} songs successfully to: ${arcpkgFile.canonicalPath}")
        }
    }
}