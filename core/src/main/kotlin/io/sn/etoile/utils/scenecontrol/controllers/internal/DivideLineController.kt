package io.sn.etoile.utils.scenecontrol.controllers.internal

import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.channels.math.ConstantChannel
import io.sn.etoile.utils.scenecontrol.channels.math.const
import io.sn.etoile.utils.scenecontrol.controllers.common.SpriteController

class DivideLineController(id: Int) : SpriteController("divline$id${id + 1}") {
    override var active: ValueChannel = if (id in 1..3) 1f.const() else 0f.const()

    override var translationX: ValueChannel = (-2.38f * id + 4.76f).const()
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var scaleX: ValueChannel = ConstantChannel(0.5587685f)
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var scaleY: ValueChannel = ConstantChannel(12.43724f)
        set(value) {
            field = value
            enablePositionModule = true
        }
}