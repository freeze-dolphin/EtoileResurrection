package io.sn.etoile.utils.scenecontrol.channels.effect

import io.sn.aetherium.utils.EaseFunc

data class Key(
    val timing: Long,
    val value: Float,
    val easing: EaseFunc,
    val overrideIndex: Int = 0,
) : Comparable<Key> {
    fun serialize(): String = "$timing,$value,${getEasingString(easing)}"

    override fun compareTo(other: Key): Int {
        if (timing == other.timing) {
            return overrideIndex.compareTo(other.overrideIndex)
        }
        return timing.compareTo(other.timing)
    }
}