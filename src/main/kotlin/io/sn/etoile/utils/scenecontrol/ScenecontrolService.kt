package io.sn.etoile.utils.scenecontrol

import com.tairitsu.compose.arcaea.Scenecontrol
import com.tairitsu.compose.arcaea.TimingGroup
import io.sn.etoile.utils.json
import io.sn.etoile.utils.jsonMinified
import io.sn.etoile.utils.scenecontrol.controllers.Scene
import io.sn.etoile.utils.scenecontrol.io.ScenecontrolSerialization
import io.sn.etoile.utils.scenecontrol.io.SerializedUnit
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import org.jetbrains.annotations.TestOnly

class ScenecontrolService(
    val scenecontrols: List<Scenecontrol>,
    val timingGroups: List<TimingGroup>,
    private val ratingClass: Int,
) {
    private val referencedControllers: MutableList<ISceneController> = mutableListOf()

    val scene: Scene = Scene(this)
    val context: Context = Context(this)

    private fun processScenecontrol(scenecontrol: Scenecontrol) {
        val handler = ScenecontrolHandler.fromScenecontrolType(scenecontrol.type, scene)
        handler?.execute(scenecontrol)
    }

    fun addReferencedController(controller: ISceneController) {
        if (!referencedControllers.contains(controller)) {
            referencedControllers.add(controller)
        }
    }

    fun serialize(): List<SerializedUnit>? {
        scenecontrols.forEach {
            processScenecontrol(it)
        }

        val serialization = ScenecontrolSerialization()
        if (referencedControllers.isEmpty()) {
            ScenecontrolHandler.clearCache()
            scene.clearNoteGroupControllerCache()
            return null
        }

        referencedControllers.forEach {
            serialization.addUnitAndGetId(it)
        }

        // clear cache for next serialization
        ScenecontrolHandler.clearCache()
        scene.clearNoteGroupControllerCache()

        return serialization.getResult()
    }

    /**
     * Assets/Scripts/Gameplay/Scenecontrol/ScenecontrolService.cs#Export()
     */
    fun export(): String? {
        val rst = serialize() ?: return null

        return jsonMinified.encodeToString(rst)
    }

    @TestOnly
    fun exportPrettified(): String? {
        val rst = serialize() ?: return null

        return json.encodeToString(rst)
    }
}