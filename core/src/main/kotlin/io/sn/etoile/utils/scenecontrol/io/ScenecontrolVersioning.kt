package io.sn.etoile.utils.scenecontrol.io

import io.sn.etoile.utils.scenecontrol.Union

/**
 * Assets/Scripts/Gameplay/Scenecontrol/IO/EnabledFeatures.cs
 */
enum class EnabledFeatures(val value: Long) {
    NONE(0),
    JUDGE_MANIPULATION(1),
    ALL(NONE or JUDGE_MANIPULATION);

    private infix fun or(enabledFeatures: EnabledFeatures): Long {
        return this.value or enabledFeatures.value
    }
}

/**
 * Assets/Scripts/Gameplay/Scenecontrol/IO/ScenecontrolVersioning.cs
 */
class ScenecontrolVersioning(
    private val features: EnabledFeatures,
) : ISerializableUnit {

    override fun serializeProperties(serialization: ScenecontrolSerialization): List<Union> {
        return listOf(Union(features.value))
    }

}