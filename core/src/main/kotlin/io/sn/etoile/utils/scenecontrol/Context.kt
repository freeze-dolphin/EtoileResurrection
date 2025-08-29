package io.sn.etoile.utils.scenecontrol

import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.channels.context.ScreenIs16By9Channel
import io.sn.etoile.utils.scenecontrol.channels.math.ConstantChannel
import io.sn.etoile.utils.scenecontrol.io.ISerializableUnit
import io.sn.etoile.utils.scenecontrol.io.ScenecontrolSerialization

/**
 * Assets/Scripts/Gameplay/Scenecontrol/Context.cs
 */
class Context(private val scenecontrolService: ScenecontrolService) : ISerializableUnit, ISceneController {

    var laneFrom: ValueChannel = ConstantChannel(1f)
        set(value) {
            field = value
            scenecontrolService.addReferencedController(this)
        }

    var laneTo: ValueChannel = ConstantChannel(4f)
        set(value) {
            field = value
            scenecontrolService.addReferencedController(this)
        }

    companion object {
        val is16By9 = ScreenIs16By9Channel()
    }

    override var serializedType: String = "context"

    override fun serializeProperties(serialization: ScenecontrolSerialization): List<Union> =
        listOf(
            Union(serialization.addUnitAndGetId(laneFrom)),
            Union(serialization.addUnitAndGetId(laneTo))
        )
}