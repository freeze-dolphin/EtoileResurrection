package io.sn.etoile.utils.scenecontrol.controllers.internal

import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.channels.math.ConstantChannel
import io.sn.etoile.utils.scenecontrol.channels.math.const
import io.sn.etoile.utils.scenecontrol.controllers.PartSide
import io.sn.etoile.utils.scenecontrol.controllers.common.SpriteController

class LaneExtraController(side: PartSide) : SpriteController("extra${side.char.uppercase()}") {
    override var active: ValueChannel = 0f.const()

    override var translationX: ValueChannel = ConstantChannel(-side.value * 5.96f)
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var scaleX: ValueChannel = 239f.const()
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var scaleY: ValueChannel = 15.35f.const()
        set(value) {
            field = value
            enablePositionModule = true
        }
}
