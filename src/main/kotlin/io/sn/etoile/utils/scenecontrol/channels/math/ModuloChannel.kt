package io.sn.etoile.utils.scenecontrol.channels.math

import io.sn.etoile.utils.scenecontrol.Union
import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.io.ScenecontrolSerialization

/**
 * Assets/Scripts/Gameplay/Scenecontrol/Channels/MathChannels/ModuloChannel.cs
 */
class ModuloChannel(private val a: ValueChannel, private val b: ValueChannel) : ValueChannel() {
    override fun valueAt(timing: Long): Float = a.valueAt(timing) % b.valueAt(timing)

    override fun serializeProperties(serialization: ScenecontrolSerialization): List<Union> =
        listOf(
            Union(serialization.addUnitAndGetId(a)),
            Union(serialization.addUnitAndGetId(b))
        )

    override fun getChildrenChannels(): Iterable<ValueChannel> = listOf(a, b)
}