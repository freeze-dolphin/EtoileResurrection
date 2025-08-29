package io.sn.etoile.utils.scenecontrol.controllers.internal

import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.channels.math.ConstantChannel
import io.sn.etoile.utils.scenecontrol.channels.math.const
import io.sn.etoile.utils.scenecontrol.controllers.ITrackController
import io.sn.etoile.utils.scenecontrol.controllers.common.SpriteController

class TrackController : SpriteController("track"), ITrackController {
    override var active: ValueChannel = 1f.const()

    override var enableTrackModule: Boolean = false
    override var edgeLAlpha: ValueChannel = ConstantChannel(255f)
        set(value) {
            field = value
            enableTrackModule = true
        }
    override var edgeRAlpha: ValueChannel = ConstantChannel(255f)
        set(value) {
            field = value
            enableTrackModule = true
        }
    override var lane1Alpha: ValueChannel = ConstantChannel(255f)
        set(value) {
            field = value
            enableTrackModule = true
        }
    override var lane2Alpha: ValueChannel = ConstantChannel(255f)
        set(value) {
            field = value
            enableTrackModule = true
        }
    override var lane3Alpha: ValueChannel = ConstantChannel(255f)
        set(value) {
            field = value
            enableTrackModule = true
        }
    override var lane4Alpha: ValueChannel = ConstantChannel(255f)
        set(value) {
            field = value
            enableTrackModule = true
        }

    override var translationZ: ValueChannel = ConstantChannel(53.5f)
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var rotationX: ValueChannel = ConstantChannel(-90f)
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var scaleX: ValueChannel = ConstantChannel(1.7896f)
        set(value) {
            field = value
            enablePositionModule = true
        }
}