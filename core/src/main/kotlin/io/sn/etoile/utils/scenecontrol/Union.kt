package io.sn.etoile.utils.scenecontrol

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable(UnionSerializer::class)
class Union {

    var string: String? = null
    var primitive: PrimitiveUnion? = null

    var isNull: Boolean = false

    class PrimitiveUnion {
        var int: Int = 0
        var long: Long = 0
        var float: Float = 0.0F
        var boolean: Boolean = false

        var intInited = false
        var longInited = false
        var floatInited = false
        var booleanInited = false

        constructor(int: Int) {
            this.int = int
            intInited = true
        }

        constructor(long: Long) {
            this.long = long
            longInited = true
        }

        constructor(float: Float) {
            this.float = float
            floatInited = true
        }

        constructor(boolean: Boolean) {
            this.boolean = boolean
            booleanInited = true
        }
    }

    companion object {
        fun nullValue(): Union {
            return Union().apply { isNull = true }
        }
    }

    constructor()

    constructor(stringOrNull: String?) {
        if (stringOrNull == null) {
            isNull = true
            return
        }

        this.string = stringOrNull
    }

    constructor(primitive: PrimitiveUnion) {
        this.primitive = primitive
    }

    constructor(int: Int) {
        this.primitive = PrimitiveUnion(int)
    }

    constructor(long: Long) {
        this.primitive = PrimitiveUnion(long)
    }

    constructor(float: Float) {
        this.primitive = PrimitiveUnion(float)
    }

    constructor(boolean: Boolean) {
        this.primitive = PrimitiveUnion(boolean)
    }

    override fun toString(): String {
        return when {
            isNull -> "null"
            string != null -> string!!
            primitive != null -> when {
                primitive!!.intInited -> primitive!!.int.toString()
                primitive!!.longInited -> primitive!!.long.toString()
                primitive!!.floatInited -> primitive!!.float.toString()
                primitive!!.booleanInited -> primitive!!.boolean.toString()
                else -> "err!"
            }

            else -> "err!"
        }
    }

}

object UnionSerializer : KSerializer<Union> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Union", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Union) {
        when {
            value.isNull -> {
                encoder.encodeNull()
                return
            }

            value.string != null -> {
                encoder.encodeString(value.string!!)
                return
            }

            value.primitive != null && value.primitive!!.intInited -> {
                encoder.encodeInt(value.primitive!!.int)
                return
            }

            value.primitive != null && value.primitive!!.longInited -> {
                encoder.encodeLong(value.primitive!!.long)
                return
            }

            value.primitive != null && value.primitive!!.floatInited -> {
                encoder.encodeFloat(value.primitive!!.float)
                return
            }

            value.primitive != null && value.primitive!!.booleanInited -> {
                encoder.encodeBoolean(value.primitive!!.boolean)
                return
            }

        }
    }

    @ExperimentalSerializationApi
    override fun deserialize(decoder: Decoder): Union {
        val jsonDecoder = decoder as? JsonDecoder ?: throw SerializationException("Union type can only be used with JSON")

        return when (val element: JsonElement = jsonDecoder.decodeJsonElement()) {
            is JsonNull -> Union.nullValue()
            is JsonPrimitive -> {
                if (element.isString) {
                    Union(element.content)
                } else if (element.booleanOrNull != null) {
                    Union(element.boolean)
                } else if (element.intOrNull != null) {
                    Union(element.int)
                } else if (element.floatOrNull != null) {
                    Union(element.float)
                } else {
                    throw SerializationException("Unknown primitive type in JSON")
                }
            }

            else -> throw SerializationException("Unsupported JSON element type")
        }
    }
}