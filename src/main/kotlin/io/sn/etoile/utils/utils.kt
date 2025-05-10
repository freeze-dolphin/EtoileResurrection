package io.sn.etoile.utils

import com.charleskorn.kaml.*
import com.tairitsu.compose.arcaea.LocalizedString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class Songlist(
    val songs: List<SonglistEntry>,
)

@Serializable
data class Packlist(
    val packs: List<PacklistEntry>,
)

@Serializable
data class PacklistEntry(
    val id: String,
    val section: String = "sidestory",
    @SerialName("plus_character") val plusCharacter: Int,
    @SerialName("is_extend_pack") val isExtendPack: Boolean,
    @SerialName("custom_banner") val customBanner: Boolean,
    @SerialName("name_localized") val nameLocalized: LocalizedString,
    @SerialName("description_localized") val descriptionLocalized: LocalizedString,
) {
    companion object {
        fun fromDefaultPacklistEntry(id: String, nameLocalized: LocalizedString, descriptionLocalized: LocalizedString) = PacklistEntry(
            id = id,
            plusCharacter = -1,
            isExtendPack = true,
            customBanner = false,
            nameLocalized = nameLocalized,
            descriptionLocalized = descriptionLocalized
        )
    }
}

@Serializable
data class DifficultyEntry(
    @Transient var _audioPath: String? = null,
    @Transient var _jacketPath: String? = null,
    @Transient var _chartPath: String? = null,

    val ratingClass: Int,
    val chartDesigner: String,
    val jacketDesigner: String,
    val rating: Int,

    var ratingPlus: Boolean? = null,
    @SerialName("title_localized") var titleLocalized: LocalizedString? = null,
    var artist: String? = null,
    @SerialName("bpm") var bpmText: String? = null,
    @SerialName("bpm_base") var bpmBase: Float? = null,
    var jacketOverride: Boolean? = null,
    var audioOverride: Boolean? = null,
    var audioPreview: Long? = null,
    var audioPreviewEnd: Long? = null,
    var side: Int? = null,
    var bg: String? = null,
)

@Serializable
data class SonglistEntry(
    val id: String,
    @SerialName("title_localized") val titleLocalized: LocalizedString? = null,
    val artist: String? = null,
    @SerialName("bpm") val bpmText: String? = null,
    @SerialName("bpm_base") val bpmBase: Float? = null,
    val set: String? = null,
    val purchase: String? = null,
    val audioPreview: Long? = null,
    val audioPreviewEnd: Long? = null,
    val side: Int? = null,
    val bg: String? = null,
    val date: Long? = null,
    val version: String? = null,
    val difficulties: List<DifficultyEntry>? = null,

    val deleted: Boolean? = null,
)

/*
 ArcCreate Data Structure
 */

@Serializable
enum class ArcpkgEntryType {
    @SerialName("level")
    LEVEL,

    @SerialName("pack")
    PACK;
}

@Serializable(ImportInformationEntrySerializer::class)
data class ImportInformationEntry(
    val directory: String, val identifier: String, val settingsFile: String, val version: Int = 0, val type: ArcpkgEntryType,
)

class ImportInformationEntrySerializer : KSerializer<ImportInformationEntry> {
    override val descriptor = buildClassSerialDescriptor("ImportInformationEntry") {
        element<String>("directory")
        element<String>("identifier")
        element<String>("settingsFile")
        element<Int>("version")
        element<ArcpkgEntryType>("type")
    }

    override fun deserialize(decoder: Decoder): ImportInformationEntry {
        val structure = decoder.beginStructure(descriptor)

        val result = (structure as YamlInput).node.yamlMap.let {
            val version = if (it nullableContent "version" != null) (it content "version").toInt() else 0

            ImportInformationEntry(
                directory = it content "directory",
                identifier = it content "identifier",
                settingsFile = it content "settingsFile",
                version = version,
                type = ArcpkgEntryType.valueOf((it content "type").uppercase())
            )
        }

        structure.endStructure(descriptor)

        return result
    }

    override fun serialize(encoder: Encoder, value: ImportInformationEntry) {
        val structure = encoder.beginStructure(descriptor)
        structure.encodeStringElement(descriptor, 0, value.directory)
        structure.encodeStringElement(descriptor, 1, value.identifier)
        structure.encodeStringElement(descriptor, 2, value.settingsFile)

        if (value.version != 0) {
            structure.encodeIntElement(descriptor, 3, value.version)
        }

        structure.encodeSerializableElement(descriptor, 4, ArcpkgEntryType.serializer(), value.type)


        structure.endStructure(descriptor)
        return
    }

}

@Serializable
data class DifficultySkin(
    val side: SideStyle = SideStyle.LIGHT,
    val note: NoteStyle = NoteStyle.NONE,
    val particle: ParticleStyle = ParticleStyle.NONE,
    val track: TrackStyle = TrackStyle.NONE,
    val accent: AccentStyle = AccentStyle.NONE,
    val singleLine: SingleLineStyle? = null,
) {

    @Serializable
    enum class SideStyle {
        @SerialName("light")
        LIGHT,

        @SerialName("conflict")
        CONFLICT,

        @SerialName("colorless")
        COLORLESS,

        @SerialName("lephon")
        LEPHON;

        fun toTrackStyle() = when (this) {
            LIGHT -> TrackStyle.LIGHT
            CONFLICT -> TrackStyle.CONFLICT
            COLORLESS -> TrackStyle.COLORLESS
            LEPHON -> TrackStyle.LIGHT
        }

        fun toParticleStyle() = when (this) {
            LIGHT -> ParticleStyle.LIGHT
            CONFLICT -> ParticleStyle.CONFLICT
            COLORLESS -> ParticleStyle.COLORLESS
            LEPHON -> ParticleStyle.LIGHT
        }
    }

    @Serializable
    enum class NoteStyle {
        @SerialName("")
        NONE,

        @SerialName("light")
        LIGHT,

        @SerialName("conflict")
        CONFLICT,
    }

    @Serializable
    enum class ParticleStyle {
        @SerialName("")
        NONE,

        @SerialName("light")
        LIGHT,

        @SerialName("conflict")
        CONFLICT,

        @SerialName("colorless")
        COLORLESS,

        @SerialName("mirailight")
        MIRAI_LIGHT,

        @SerialName("miraiconflict")
        MIRAI_CONFLICT
    }

    @Serializable
    enum class TrackStyle {
        @SerialName("")
        NONE,

        @SerialName("light")
        LIGHT,

        @SerialName("conflict")
        CONFLICT,

        @SerialName("black")
        BLACK,

        @SerialName("nijuusei")
        NIJUUSEI,

        @SerialName("rei")
        REI,

        @SerialName("conflictvs")
        CONFLICT_VS,

        @SerialName("tempestissimo")
        TEMPESTISSIMO,

        @SerialName("finale")
        FINALE,

        @SerialName("pentiment")
        PENTIMENT,

        @SerialName("arcana")
        ARCANA,

        @SerialName("colorless")
        COLORLESS,
    }

    @Serializable
    enum class AccentStyle {
        @SerialName("")
        NONE,

        @SerialName("light")
        LIGHT,

        @SerialName("conflict")
        CONFLICT,

        @SerialName("dynamix")
        DYNAMIX,

        @SerialName("colorless")
        COLORLESS,
    }

    @Serializable
    enum class SingleLineStyle {
        @SerialName("")
        NONE,

        @SerialName("light")
        LIGHT,

        @SerialName("conflict")
        CONFLICT,

        @SerialName("neo")
        NEO
    }

}

@Serializable
data class ChartEntry(
    val chartPath: String,
    val audioPath: String,
    val jacketPath: String,
    val backgroundPath: String? = null,
    val baseBpm: Float,
    val bpmText: String,
    val syncBaseBpm: Boolean = false,
    val title: String,
    val composer: String,
    val alias: String? = null,
    val charter: String? = null,
    val illustrator: String? = null,
    val difficulty: String,
    val chartConstant: Float? = null,
    val difficultyColor: String,
    val skin: DifficultySkin? = DifficultySkin(),
    val previewStart: Long? = 0,
    val previewEnd: Long? = 5000,
    val searchTags: String? = null,
)

@Serializable
data class ProjectInformation(
    val lastOpenedChartPath: String, val charts: List<ChartEntry>,
)

fun getCurrentSystemTime(): Long = System.currentTimeMillis() / 1000L

fun file(vararg dirs: String): File = dirs.reduce { acc, next ->
    File(acc, next).path
}.let {
    File(it)
}

val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

val jsonMinified = Json {
    prettyPrint = false
    ignoreUnknownKeys = true
}

val yaml = Yaml(
    configuration = YamlConfiguration(
        allowAnchorsAndAliases = true, // fix #3
        strictMode = false
    )
)

infix fun YamlNode.content(path: String): String {
    return this.yamlMap.get<YamlNode>(path)!!.yamlScalar.content
}

infix fun YamlNode.nullableContent(path: String): String? {
    return this.yamlMap.get<YamlNode>(path)?.yamlScalar?.content
}