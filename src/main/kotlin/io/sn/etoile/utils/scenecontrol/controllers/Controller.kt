package io.sn.etoile.utils.scenecontrol.controllers

import io.sn.etoile.utils.scenecontrol.ISceneController
import io.sn.etoile.utils.scenecontrol.Union
import io.sn.etoile.utils.scenecontrol.io.ISerializableUnit
import io.sn.etoile.utils.scenecontrol.io.ScenecontrolSerialization

/**
 * Assets/Scripts/Gameplay/Scenecontrol/Controllers/Controller.cs
 */
abstract class Controller : ISerializableUnit, IController, ISceneController {
    companion object {
        private fun addIdLookupToResult(
            serialization: ScenecontrolSerialization,
            target: MutableList<Union>,
            channels: List<ISerializableUnit>
        ) {
            target.addAll(channels.map {
                Union(serialization.addUnitAndGetId(it))
            })
        }
    }

    override fun serializeProperties(serialization: ScenecontrolSerialization): List<Union>? {
        val result: MutableList<Union> = mutableListOf(Union(null))
        result.add(Union(serialization.addUnitAndGetId(active)))

        if (this is IPositionController) {
            result.add(Union(enablePositionModule))
            addIdLookupToResult(
                serialization, result, listOf(
                    translationX,
                    translationY,
                    translationZ,
                    rotationX,
                    rotationY,
                    rotationZ,
                    scaleX,
                    scaleY,
                    scaleZ,
                )
            )
        }

        if (this is IColorController) {
            result.add(Union(enableColorModule))
            addIdLookupToResult(
                serialization, result, listOf(
                    colorR,
                    colorG,
                    colorB,
                    colorA,
                    colorH,
                    colorS,
                    colorV,
                )
            )
        }

        if (this is ILayerController) {
            result.add(Union(enableLayerModule))
            addIdLookupToResult(
                serialization, result, listOf(
                    layer,
                    sort,
                    alpha
                )
            )
        }

        if (this is INoteGroupController) {
            result.add(Union(enableNoteGroupModule))
            addIdLookupToResult(
                serialization, result, listOf(
                    angleX,
                    angleY,
                    rotationIndividualX,
                    rotationIndividualY,
                    rotationIndividualZ,
                    scaleIndividualX,
                    scaleIndividualY,
                    scaleIndividualZ,
                    judgeSizeX,
                    judgeSizeY,
                    judgeOffsetX,
                    judgeOffsetY,
                    judgeOffsetZ,
                )
            )
        }

        if (this is ICameraController) {
            result.add(Union(enableCameraModule))
            addIdLookupToResult(
                serialization, result, listOf(
                    fieldOfView,
                    tiltFactor
                )
            )
        }

        if (this is ITextureController) {
            result.add(Union(enableTextureModule))
            addIdLookupToResult(
                serialization, result, listOf(
                    textureOffsetX,
                    textureOffsetY,
                    textureScaleX,
                    textureScaleY,
                )
            )
        }

        if (this is ITrackController) {
            result.add(Union(enableTrackModule))
            addIdLookupToResult(
                serialization, result, listOf(
                    edgeLAlpha,
                    edgeRAlpha,
                    lane1Alpha,
                    lane2Alpha,
                    lane3Alpha,
                    lane4Alpha,
                )
            )
            result.add(Union.nullValue())
        }

        return result
    }
}