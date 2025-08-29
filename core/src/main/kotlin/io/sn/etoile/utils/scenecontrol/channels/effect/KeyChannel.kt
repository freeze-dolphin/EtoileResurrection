package io.sn.etoile.utils.scenecontrol.channels.effect

import io.sn.aetherium.utils.EasingFunction
import io.sn.aetherium.utils.linear
import io.sn.etoile.utils.scenecontrol.Union
import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.io.ScenecontrolSerialization

/**
 * Assets/Scripts/Gameplay/Scenecontrol/Channels/MathChannels/KeyChannel.cs
 *
 * this implementation has no cache design
 */
class KeyChannel : ValueChannel() {

    val keys: MutableList<Key> = mutableListOf()
    private var defaultEasing: EasingFunction? = null

    fun setDefaultEasing(easing: EasingFunction) {
        defaultEasing = easing
    }

    fun addKey(timing: Long, value: Float, easing: EasingFunction? = null) {
        val e: EasingFunction = easing ?: (defaultEasing ?: linear)

        val keyAtSameTiming = keys.filter { it.timing == timing }
        var overrideIndex = keyAtSameTiming.maxOfOrNull { it.overrideIndex } ?: 0
        if (keys.isNotEmpty() && keyAtSameTiming.isNotEmpty()) { // overlapping
            overrideIndex += 1
        }

        val key = Key(timing, value, e, overrideIndex)
        keys.add(key)

        keys.sort()
    }

    override fun toString(): String {
        return keys.toTypedArray().contentDeepToString()
    }

    override fun valueAt(timing: Long): Float {
        if (keys.isEmpty()) return 0f
        if (keys.size == 1) return keys.last().value
        if (timing <= keys.first().timing) return keys.first().value
        if (timing >= keys.last().timing) return keys.last().value

        val idx = keys.indexOfLast { it.timing <= timing }

        val timing1 = keys[idx].timing
        val timing2 = keys[idx + 1].timing

        val key1 = keys[idx]
        val key2 = keys[idx + 1]

        if (timing1 == timing2) return if (key1.overrideIndex > key2.overrideIndex) key1.value else key2.value

        val p: Float = (timing - timing1).toFloat() / (timing2 - timing1)

        return key1.value + key1.easing(p.toDouble()).toFloat() * (key2.value - key1.value)
    }

    override fun serializeProperties(serialization: ScenecontrolSerialization): List<Union> = keys.map {
        Union(it.serialize())
    }

    override fun getChildrenChannels(): Iterable<ValueChannel> = listOf()
}