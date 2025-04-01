package io.sn.etoile.utils.scenecontrol.channels.math

import io.sn.etoile.utils.scenecontrol.Union
import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.io.ScenecontrolSerialization

/**
 * Assets/Scripts/Gameplay/Scenecontrol/Channels/MathChannels/InverseChannel.cs
 */
class InverseChannel(private val target: ValueChannel) : ValueChannel() {
    override fun valueAt(timing: Long): Float = 1 / target.valueAt(timing)

    override fun serializeProperties(serialization: ScenecontrolSerialization): List<Union> =
        listOf(
            Union(serialization.addUnitAndGetId(target))
        )

    override fun getChildrenChannels(): Iterable<ValueChannel> = listOf(target)
}