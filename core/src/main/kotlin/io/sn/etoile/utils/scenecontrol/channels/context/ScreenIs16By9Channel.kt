package io.sn.etoile.utils.scenecontrol.channels.context

import io.sn.etoile.utils.scenecontrol.Union
import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.io.ScenecontrolSerialization

class ScreenIs16By9Channel : ValueChannel() {
    override fun valueAt(timing: Long): Float = 0f

    override fun serializeProperties(serialization: ScenecontrolSerialization): List<Union>? = null

    override fun getChildrenChannels(): Iterable<ValueChannel> = listOf()
}