package io.sn.etoile.utils.scenecontrol.io

import io.sn.etoile.utils.scenecontrol.Context
import io.sn.etoile.utils.scenecontrol.ISceneController
import io.sn.etoile.utils.scenecontrol.Union
import io.sn.etoile.utils.scenecontrol.channels.context.ScreenIs16By9Channel
import io.sn.etoile.utils.scenecontrol.channels.effect.KeyChannel
import io.sn.etoile.utils.scenecontrol.channels.math.ConstantChannel
import io.sn.etoile.utils.scenecontrol.channels.math.NegateChannel
import io.sn.etoile.utils.scenecontrol.channels.math.ProductChannel
import io.sn.etoile.utils.scenecontrol.channels.math.SumChannel
import kotlinx.serialization.*

/**
 * Assets/Scripts/Gameplay/Scenecontrol/IO/SerializedUnit.cs
 */
@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class SerializedUnit(
    @SerialName("Type")
    val type: String,

    @SerialName("Properties")
    @EncodeDefault
    val properties: List<Union>? = null,
) {
    companion object {
        fun placeholder(): SerializedUnit {
            return SerializedUnit(type = "", properties = emptyList())
        }
    }
}

/**
 * Assets/Scripts/Gameplay/Scenecontrol/IO/ISerializableUnit.cs
 */
interface ISerializableUnit {
    fun serializeProperties(serialization: ScenecontrolSerialization): List<Union>?
    // fun deserializeProperties()
}

/**
 * Assets/Scripts/Gameplay/Scenecontrol/IO/ScenecontrolSerialization.cs
 */
class ScenecontrolSerialization {
    private val units: MutableList<ISerializableUnit> = mutableListOf()
    private val serializedUnits: MutableList<SerializedUnit>
    private val idLookup: MutableMap<ISerializableUnit, Int> = mutableMapOf()

    fun getResult(): List<SerializedUnit> = serializedUnits

    init {
        val versioning = ScenecontrolVersioning(EnabledFeatures.ALL)
        units.add(versioning)
        idLookup[versioning] = 0
        serializedUnits = mutableListOf(
            SerializedUnit(
                type = getTypeFromUnit(versioning),
                properties = versioning.serializeProperties(this)
            )
        )
    }

    fun addUnitAndGetId(unit: ISerializableUnit): Int {
        idLookup[unit]?.let { return it }

        units.add(unit)
        val id = units.size - 1
        idLookup[unit] = id
        serializedUnits.add(SerializedUnit.placeholder())
        serializedUnits[id] = SerializedUnit(
            type = getTypeFromUnit(unit),
            properties = unit.serializeProperties(this)
        )

        return id
    }

    companion object {
        fun ISerializableUnit.serialize(serialization: ScenecontrolSerialization): SerializedUnit = SerializedUnit(
            type = getTypeFromUnit(this),
            properties = this.serializeProperties(serialization)
        )

        fun getTypeFromUnit(unit: ISerializableUnit): String {
            when (unit) {
                is ScenecontrolVersioning -> return "versioning"
                is Context -> return "context"

                is ConstantChannel -> return "channel.const"
                is NegateChannel -> return "channel.negate"
                is ProductChannel -> return "channel.product"
                is SumChannel -> return "channel.sum"
                is KeyChannel -> return "channel.key"

                is ScreenIs16By9Channel -> return "channel.context.is16by9"

                else -> {
                    if (unit is ISceneController) {
                        return unit.serializedType
                    }

                    throw SerializationException("Unknown unit: $unit")
                }
            }
        }
    }
}