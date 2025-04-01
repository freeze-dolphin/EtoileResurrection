package io.sn.etoile.utils.scenecontrol.channels.string

import io.sn.etoile.utils.scenecontrol.Union
import io.sn.etoile.utils.scenecontrol.io.ISerializableUnit
import io.sn.etoile.utils.scenecontrol.io.ScenecontrolSerialization

abstract class StringChannel : ISerializableUnit {
    abstract fun valueAt(timing: Long): String
    abstract override fun serializeProperties(serialization: ScenecontrolSerialization): List<Union>?
}