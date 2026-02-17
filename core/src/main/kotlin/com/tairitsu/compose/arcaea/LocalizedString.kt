package com.tairitsu.compose.arcaea

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KProperty

/**
 * Localized string for Arcaea song data.
 */
@Serializable(with = LocalizedString.LocalizedStringSerializer::class)
class LocalizedString : Map<String, String> {
    /**
     * Localized string internal storage map.
     */
    internal var storage: LinkedHashMap<String, String> = LinkedHashMap()

    /**
     * Create a new localized string by giving [map]
     */
    internal constructor(kv: LinkedHashMap<String, String>) {
        storage = kv
    }

    /**
     * Create a new localized string. English locale is required.
     */
    constructor(enLocale: String) {
        storage = LinkedHashMap()
        storage["en"] = enLocale
    }

    /**
     * Create a new localized string. English locale is required.
     * Use a closure to specific other languages
     */
    constructor(enLocale: String, closure: LocalizedString.() -> Unit) {
        storage = LinkedHashMap()
        storage["en"] = enLocale
        closure.invoke(this)
    }

    /**
     * Localized string in English locale.
     */
    var en by LocaleGetting

    /**
     * Localized string in Japanese locale.
     */
    var ja by LocaleGetting

    /**
     * Localized string in Chinese locale.
     */
    var ko by LocaleGetting

    /**
     * Localized string in Simplified Chinese locale.
     */
    var zhHans by LocaleGetting

    /**
     * Localized string in Traditional Chinese locale.
     */
    var zhHant by LocaleGetting

    /**
     * Set localized string
     */
    internal operator fun set(key: String, value: String) {
        storage[key] = value
    }

    // From Map

    override val size: Int = storage.size
    override fun isEmpty(): Boolean = storage.isEmpty()
    override fun containsKey(key: String): Boolean = storage.containsKey(key)
    override fun containsValue(value: String): Boolean = storage.containsValue(value)
    override fun get(key: String): String? = storage[key]

    override val keys: MutableSet<String> = storage.keys
    override val values: MutableCollection<String> = storage.values
    override val entries: MutableSet<MutableMap.MutableEntry<String, String>> = storage.entries

    /**
     * Delegate for localized string.
     */
    private object LocaleGetting {
        private val localeNameMapping = mapOf(
            "en" to "en", "ja" to "ja", "ko" to "ko", "zhHans" to "zh-Hans", "zhHant" to "zh-Hant"
        )

        operator fun getValue(obj: LocalizedString, property: KProperty<*>): String =
            obj[localeNameMapping[property.name]!!] ?: obj.en

        operator fun setValue(obj: LocalizedString, property: KProperty<*>, value: String) {
            obj[localeNameMapping[property.name]!!] = value
        }
    }


    /**
     * Serializer for [LocalizedString].
     */
    object LocalizedStringSerializer : KSerializer<LocalizedString> {
        private val mapSerializer =
            kotlinx.serialization.builtins.MapSerializer(String.serializer(), String.serializer())

        override fun deserialize(decoder: Decoder): LocalizedString {
            val map = LinkedHashMap(mapSerializer.deserialize(decoder))
            return LocalizedString(map)
        }

        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor
            get() = SerialDescriptor("LocalizedString", mapSerializer.descriptor)

        override fun serialize(encoder: Encoder, value: LocalizedString) {
            return mapSerializer.serialize(encoder, value.storage)
        }
    }
}