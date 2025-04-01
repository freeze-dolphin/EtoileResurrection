package io.sn.etoile.utils.scenecontrol.controllers.internal

import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.channels.math.ConstantChannel
import io.sn.etoile.utils.scenecontrol.channels.math.const
import io.sn.etoile.utils.scenecontrol.controllers.PartSide
import io.sn.etoile.utils.scenecontrol.controllers.common.SpriteController

class EdgeExtraController(side: PartSide) : SpriteController("edgeextra${side.char.uppercase()}") {
    override var active: ValueChannel = 0f.const()

    override var translationX: ValueChannel = ConstantChannel(-side.value * 7.33f)
        set(value) {
            field = value
            enablePositionModule = true
        }
}
