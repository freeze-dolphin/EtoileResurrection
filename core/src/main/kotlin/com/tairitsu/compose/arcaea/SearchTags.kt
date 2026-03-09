package com.tairitsu.compose.arcaea

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KProperty

@Serializable(with = SearchTags.SearchTagsSerializer::class)
class SearchTags : Map<String, List<String>> {
    /**
     * Localized string internal storage map.
     */
    internal var storage: LinkedHashMap<String, List<String>> = LinkedHashMap()

    /**
     * Create a new localized string by giving [map]
     */
    internal constructor(kv: LinkedHashMap<String, List<String>>) {
        storage = kv
    }

    /**
     * Create a new localized string. English locale is required.
     */
    constructor(enLocale: List<String>) {
        storage = LinkedHashMap()
        storage["en"] = enLocale
    }

    /**
     * Create a new localized string. English locale is required.
     * Use a closure to specific other languages
     */
    constructor(enLocale: List<String>, closure: SearchTags.() -> Unit) {
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
    internal operator fun set(key: String, value: List<String>) {
        storage[key] = value
    }

    // From Map

    override val size: Int = storage.size
    override fun isEmpty(): Boolean = storage.isEmpty()
    override fun containsKey(key: String): Boolean = storage.containsKey(key)
    override fun containsValue(value: List<String>): Boolean = storage.containsValue(value)
    override fun get(key: String): List<String>? = storage[key]

    override val keys: MutableSet<String> = storage.keys
    override val values: MutableCollection<List<String>> = storage.values
    override val entries: MutableSet<MutableMap.MutableEntry<String, List<String>>> = storage.entries

    /**
     * Delegate for localized string.
     */
    private object LocaleGetting {
        private val localeNameMapping = mapOf(
            "en" to "en", "ja" to "ja", "ko" to "ko", "zhHans" to "zh-Hans", "zhHant" to "zh-Hant"
        )

        operator fun getValue(obj: SearchTags, property: KProperty<*>): List<String> =
            obj[localeNameMapping[property.name]!!] ?: obj.en

        operator fun setValue(obj: SearchTags, property: KProperty<*>, value: List<String>) {
            obj[localeNameMapping[property.name]!!] = value
        }
    }


    /**
     * Serializer for [SearchTags].
     */
    object SearchTagsSerializer : KSerializer<SearchTags> {
        private val mapSerializer =
            MapSerializer(String.serializer(), ListSerializer(String.serializer()))

        override fun deserialize(decoder: Decoder): SearchTags {
            val map = LinkedHashMap(mapSerializer.deserialize(decoder))
            return SearchTags(map)
        }

        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor
            get() = SerialDescriptor("SearchTags", mapSerializer.descriptor)

        override fun serialize(encoder: Encoder, value: SearchTags) {
            return mapSerializer.serialize(encoder, value.storage)
        }
    }
}