package io.sn.etoile.utils.scenecontrol.controllers

import io.sn.etoile.utils.scenecontrol.ScenecontrolService
import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.channels.math.const
import io.sn.etoile.utils.scenecontrol.controllers.internal.*

/**
 * Assets/Scripts/Gameplay/Scenecontrol/Controllers/Scene.cs
 */
class Scene(val scenecontrolService: ScenecontrolService) {
    val gameplayCamera = CameraController()
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }

    val skyInputLine = SkyInputLineController()
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }

    val skyInputLabel = SkyInputLabelController()
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }

    val singleLineL = SingleLineController(PartSide.LEFT)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }

    val singleLineR = SingleLineController(PartSide.RIGHT)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }

    val beatlines = BeatlinesController()
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }

    val track = TrackController()
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }

    val divideLine01 = DivideLineController(0)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }
    val divideLine12 = DivideLineController(1)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }
    val divideLine23 = DivideLineController(2)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }
    val divideLine34 = DivideLineController(3)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }
    val divideLine45 = DivideLineController(4)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }

    val criticalLine0 = CriticalLineController(0)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }
    val criticalLine1 = CriticalLineController(1)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }
    val criticalLine2 = CriticalLineController(2)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }
    val criticalLine3 = CriticalLineController(3)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }
    val criticalLine4 = CriticalLineController(4)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }
    val criticalLine5 = CriticalLineController(5)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }

    val laneExtraL = LaneExtraController(PartSide.LEFT)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }
    val laneExtraR = LaneExtraController(PartSide.RIGHT)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }

    val edgeExtraL = EdgeExtraController(PartSide.LEFT)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }
    val edgeExtraR = EdgeExtraController(PartSide.RIGHT)
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }

    var isLaneExtraEnabled: ValueChannel = 0f.const()

    val darken = DarkenController()
        get() {
            scenecontrolService.addReferencedController(field)
            return field
        }

    private val noteGroupControllerCache = mutableMapOf<Int, NoteGroupController>()

    fun clearNoteGroupControllerCache() {
        noteGroupControllerCache.clear()
    }

    fun getNoteGroupController(timingGroupId: Int): NoteGroupController {
        return noteGroupControllerCache.getOrPut(timingGroupId) {
            NoteGroupController("tg.$timingGroupId").apply {
                scenecontrolService.addReferencedController(this)
            }
        }
    }
}