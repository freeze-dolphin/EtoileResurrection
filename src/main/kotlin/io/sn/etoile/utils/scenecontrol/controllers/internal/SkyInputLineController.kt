package io.sn.etoile.utils.scenecontrol.controllers.internal

import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.channels.math.ConstantChannel
import io.sn.etoile.utils.scenecontrol.channels.math.const
import io.sn.etoile.utils.scenecontrol.controllers.common.SpriteController

class SkyInputLineController : SpriteController("skyinputline") {
    override var active: ValueChannel = 1f.const()

    override var translationY: ValueChannel = ConstantChannel(5.5f)
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var scaleX: ValueChannel = ConstantChannel(5000f)
        set(value) {
            field = value
            enablePositionModule = true
        }
}