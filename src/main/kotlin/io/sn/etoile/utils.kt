package io.sn.etoile

import com.tairitsu.compose.arcaea.LocalizedString
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.io.File

fun file(vararg dirs: String): File = dirs.reduce { acc, next ->
    File(acc, next).path
}.let {
    File(it)
}


@Serializable
data class Songlist(
    val songs: List<SonglistEntry>
)

@Serializable
data class Packlist(
    val packs: List<PacklistEntry>
)

@Serializable
data class PacklistEntry(
    val id: String,
    val section: String = "sidestory",
    @SerialName("plus_character") val plusCharacter: Int,
    @SerialName("is_extend_pack") val isExtendPack: Boolean,
    @SerialName("custom_banner") val customBanner: Boolean,
    @SerialName("name_localized") val nameLocalized: LocalizedString,
    @SerialName("description_localized") val descriptionLocalized: LocalizedString
) {
    companion object {
        fun fromDefaultPacklistEntry(id: String, nameLocalized: LocalizedString, descriptionLocalized: LocalizedString) =
            PacklistEntry(
                id = id,
                plusCharacter = -1,
                isExtendPack = true,
                customBanner = false,
                nameLocalized = nameLocalized,
                descriptionLocalized = descriptionLocalized
            )
    }
}

class ArcpkgEntryTypeSerializer : KSerializer<ArcpkgEntryType> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("ArcpkgEntryType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): ArcpkgEntryType {
        return ArcpkgEntryType.valueOf(decoder.decodeString().uppercase())
    }

    override fun serialize(encoder: Encoder, value: ArcpkgEntryType) {
        throw IllegalStateException("Not implemented")
    }

}

@Serializable(ArcpkgEntryTypeSerializer::class)
enum class ArcpkgEntryType {
    LEVEL, PACK;
}

@Serializable
data class IndexEntry(
    val directory: String, val identifier: String, val settingsFile: String, val version: Int = -1, val type: ArcpkgEntryType
)

@Serializable
data class DifficultyEntry(
    @Transient var audioPath: String? = null,
    @Transient var jacketPath: String? = null,
    @Transient var chartPath: String? = null,

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
    @SerialName("title_localized") val titleLocalized: LocalizedString,
    val artist: String,
    @SerialName("bpm") val bpmText: String,
    @SerialName("bpm_base") val bpmBase: Float,
    val set: String,
    val purchase: String,
    val audioPreview: Long,
    val audioPreviewEnd: Long,
    val side: Int,
    val bg: String,
    val date: Long,
    val version: String,
    val difficulties: List<DifficultyEntry>
)