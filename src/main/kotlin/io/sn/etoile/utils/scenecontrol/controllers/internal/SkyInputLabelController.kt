package io.sn.etoile.utils.scenecontrol.controllers.internal

import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.channels.math.ConstantChannel
import io.sn.etoile.utils.scenecontrol.channels.math.const
import io.sn.etoile.utils.scenecontrol.controllers.common.SpriteController

class SkyInputLabelController : SpriteController("skyinputlabel") {
    override var active: ValueChannel = 1f.const()

    override var translationX: ValueChannel = ConstantChannel(-7.1f)
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var translationY: ValueChannel = ConstantChannel(5.65f)
        set(value) {
            field = value
            enablePositionModule = true
        }
}