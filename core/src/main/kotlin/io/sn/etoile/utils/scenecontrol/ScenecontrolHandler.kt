package io.sn.etoile.utils.scenecontrol

import com.tairitsu.compose.Scenecontrol
import com.tairitsu.compose.ScenecontrolType
import com.tairitsu.compose.TimingGroup
import io.sn.aetherium.utils.linear
import io.sn.etoile.utils.scenecontrol.channels.effect.KeyChannel
import io.sn.etoile.utils.scenecontrol.channels.effect.constant
import io.sn.etoile.utils.scenecontrol.channels.math.const
import io.sn.etoile.utils.scenecontrol.controllers.Scene
import kotlin.collections.getOrPut
import kotlin.collections.indexOfFirst
import kotlin.math.roundToInt
import kotlin.properties.Delegates

abstract class ScenecontrolHandler {
    var hasSetup: Boolean = false
    abstract fun setup()
    protected fun checkSetup() {
        if (!hasSetup) {
            setup()
            hasSetup = true
        }
    }

    abstract fun execute(scenecontrol: Scenecontrol)

    companion object {
        private val handlerCache: MutableMap<ScenecontrolType, ScenecontrolHandler> = mutableMapOf()

        fun clearCache() {
            handlerCache.clear()
        }

        fun fromScenecontrolType(scenecontrolType: ScenecontrolType, scene: Scene): ScenecontrolHandler? {
            val type = scenecontrolType.let {
                when (it) { // remap
                    ScenecontrolType.TRACK_HIDE -> ScenecontrolType.TRACK_DISPLAY
                    ScenecontrolType.TRACK_SHOW -> ScenecontrolType.TRACK_DISPLAY
                    else -> it
                }
            }
            return handlerCache.getOrPut(type) {
                when (type) {
                    ScenecontrolType.TRACK_DISPLAY -> TrackDisplayHandler(scene)

                    ScenecontrolType.HIDE_GROUP -> HideGroupHandler(scene)
                    ScenecontrolType.ENWIDEN_CAMERA -> EnwidenCameraHandler(scene)
                    ScenecontrolType.ENWIDEN_LANES -> EnwidenLanesHandler(scene)

                    else -> {
                        // no need to implement
                        return null
                    }
                }
            }
        }
    }
}

/**
 * Assets/Scripts/Compose/EventsEditor/Scenecontrol/CommandTypes/EnwidenCameraType.cs
 */
class EnwidenCameraHandler(private val scene: Scene) : ScenecontrolHandler() {
    private lateinit var enwidenCameraFactor: KeyChannel

    override fun setup() {
        hasSetup = true
        enwidenCameraFactor = KeyChannel().apply {
            setDefaultEasing(linear)
            addKey(-999999, 0f)
        }

        val camera = scene.gameplayCamera
        val skyline = scene.skyInputLine
        val skyLabel = scene.skyInputLabel
        val singleL = scene.singleLineL
        val singleR = scene.singleLineR

        val yPos = (Context.is16By9 * 1.5f) + 3
        val skyDeltaY = 2.745f
        val singleDeltaX = 5f
        camera.translationY += enwidenCameraFactor * 4.5f
        camera.translationZ += enwidenCameraFactor * yPos
        skyline.translationY += enwidenCameraFactor * skyDeltaY
        skyLabel.translationY += enwidenCameraFactor * skyDeltaY
        singleL.translationX += enwidenCameraFactor * singleDeltaX
        singleR.translationX -= enwidenCameraFactor * singleDeltaX
    }

    override fun execute(scenecontrol: Scenecontrol) {
        checkSetup()

        val timing = scenecontrol.time
        val duration = scenecontrol.params[0].toInt()
        val toggle = scenecontrol.params[1]

        enwidenCameraFactor.addKey(timing, enwidenCameraFactor.valueAt(timing))
        enwidenCameraFactor.addKey(timing + duration, toggle.toFloat())
    }

}

/**
 * Assets/Scripts/Compose/EventsEditor/Scenecontrol/CommandTypes/EnwidenLanesType.cs
 */
class EnwidenLanesHandler(private val scene: Scene) : ScenecontrolHandler() {
    private lateinit var enwidenLaneFactor: KeyChannel

    override fun setup() {
        enwidenLaneFactor = KeyChannel().apply {
            setDefaultEasing(linear)
            addKey(-999999, 0f)
        }

        val alpha = -enwidenLaneFactor + 1
        val yPos = alpha * -100f

        scene.laneExtraL.active = 1f.const()
        scene.laneExtraR.active = 1f.const()
        scene.criticalLine0.active = 1f.const()
        scene.criticalLine5.active = 1f.const()
        scene.divideLine01.active = 1f.const()
        scene.divideLine45.active = 1f.const()
        scene.edgeExtraL.active = 1f.const()
        scene.edgeExtraR.active = 1f.const()

        scene.isLaneExtraEnabled = 1f.const() * enwidenLaneFactor

        scene.edgeExtraL.colorA *= enwidenLaneFactor
        scene.laneExtraL.colorA *= enwidenLaneFactor
        scene.laneExtraR.translationY += yPos
        scene.criticalLine0.colorA *= enwidenLaneFactor
        scene.divideLine01.colorA *= enwidenLaneFactor

        scene.divideLine45.colorA *= enwidenLaneFactor
        scene.criticalLine5.colorA *= enwidenLaneFactor
        scene.laneExtraR.translationY += yPos
        scene.laneExtraR.colorA *= enwidenLaneFactor
        scene.edgeExtraR.colorA *= enwidenLaneFactor

        val track = scene.track
        track.edgeLAlpha *= alpha
        track.edgeRAlpha *= alpha

        val context = scene.scenecontrolService.context
        context.laneTo += enwidenLaneFactor
        context.laneFrom -= enwidenLaneFactor

        scene.beatlines.scaleX *= (enwidenLaneFactor * 0.5f) + 1f
    }

    override fun execute(scenecontrol: Scenecontrol) {
        checkSetup()

        val timing = scenecontrol.time
        val duration = scenecontrol.params[0].toInt()
        val toggle = scenecontrol.params[1]


        enwidenLaneFactor.addKey(timing, enwidenLaneFactor.valueAt(timing))
        enwidenLaneFactor.addKey(timing + duration, toggle.toFloat())
    }

}

/**
 * Assets/Scripts/Compose/EventsEditor/Scenecontrol/CommandTypes/GroupAlphaType.cs
 * @see ScenecontrolType.HIDE_GROUP
 */
class HideGroupHandler(private val scene: Scene) : ScenecontrolHandler() {
    override fun setup() {}

    override fun execute(scenecontrol: Scenecontrol) {
        val timing = scenecontrol.time
        val isHidden = scenecontrol.params[1].toInt()

        val tgIdx = scene.scenecontrolService.timingGroups.indexOfFirst { it: TimingGroup ->
            scenecontrol in it.getScenecontrols()
        }

        val noteGroupController = scene.getNoteGroupController(tgIdx)

        var channel = noteGroupController.active.find("internal")
        if (channel == null) {
            channel = KeyChannel().apply {
                setDefaultEasing(constant)
                addKey(-999999, 1f)
                name = "internal"
            }
            noteGroupController.active *= channel
        }

        val c = channel as KeyChannel
        c.addKey(timing, (1 - isHidden).toFloat())
    }

}

/**
 * Assets/Scripts/Compose/EventsEditor/Scenecontrol/CommandTypes/TrackDisplayType.cs
 */
class TrackDisplayHandler(private val scene: Scene) : ScenecontrolHandler() {
    private lateinit var trackAlphaFactor: KeyChannel
    private lateinit var darkenAlphaFactor: KeyChannel

    override fun setup() {
        trackAlphaFactor = KeyChannel().apply {
            setDefaultEasing(linear)
            addKey(-999999, 1f)
        }

        darkenAlphaFactor = KeyChannel().apply {
            setDefaultEasing(linear)
            addKey(-999999, 0f)
        }

        arrayOf(
            scene.track,
            scene.laneExtraL,
            scene.laneExtraR,
            scene.edgeExtraL,
            scene.edgeExtraR,
            //scene.criticalLine0,
            //scene.criticalLine1,
            //scene.criticalLine2,
            //scene.criticalLine3,
            //scene.criticalLine4,
            //scene.criticalLine5,
            scene.divideLine01,
            scene.divideLine12,
            scene.divideLine23,
            scene.divideLine34,
            scene.divideLine45,
        ).forEach {
            it.colorA *= trackAlphaFactor
        }

        val darken = scene.darken
        darken.active = 1f.const()
        darken.colorR = 0f.const()
        darken.colorG = 0f.const()
        darken.colorB = 0f.const()
        darken.colorA *= darkenAlphaFactor
    }

    override fun execute(scenecontrol: Scenecontrol) {
        checkSetup()

        val timing = scenecontrol.time

        var duration by Delegates.notNull<Int>()
        var alpha by Delegates.notNull<Int>()

        when (scenecontrol.type) {
            ScenecontrolType.TRACK_HIDE -> {
                duration = 1000
                alpha = 0
            }

            ScenecontrolType.TRACK_SHOW -> {
                duration = 1000
                alpha = 255
            }

            ScenecontrolType.TRACK_DISPLAY -> {
                duration = (scenecontrol.params[0].toFloat() * 1000).roundToInt()
                alpha = scenecontrol.params[1].toInt()
            }

            else -> throw RuntimeException("Unsupported scenecontrol type: ${scenecontrol.type} for TrackDisplayHandler")
        }

        val darkenAlpha = if (alpha != 0 && alpha % 255 == 0) 0f else 1f
        val roundAlpha = alpha % 256

        trackAlphaFactor.apply {
            addKey(timing, trackAlphaFactor.valueAt(timing))
            addKey(timing + duration, roundAlpha / 255f)
        }

        darkenAlphaFactor.apply {
            addKey(timing, darkenAlphaFactor.valueAt(timing))
            addKey(timing + 250, darkenAlpha)
        }
    }

}
