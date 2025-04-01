package io.sn.etoile.utils.scenecontrol.channels.math

import io.sn.etoile.utils.scenecontrol.Union
import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.io.ScenecontrolSerialization

/**
 * Assets/Scripts/Gameplay/Scenecontrol/Channels/MathChannels/ConstantChannel.cs
 */
class ConstantChannel(private var value: Float) : ValueChannel() {
    override fun valueAt(timing: Long): Float = value

    override fun serializeProperties(serialization: ScenecontrolSerialization): List<Union> = listOf(Union(value))

    override fun getChildrenChannels(): Iterable<ValueChannel> = emptyList()
}

fun Float.const(): ConstantChannel {
    return ConstantChannel(this)
}