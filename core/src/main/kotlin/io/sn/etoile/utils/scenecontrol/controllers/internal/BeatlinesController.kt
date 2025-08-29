package io.sn.etoile.utils.scenecontrol.controllers.internal

import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.channels.math.ConstantChannel
import io.sn.etoile.utils.scenecontrol.channels.math.const
import io.sn.etoile.utils.scenecontrol.controllers.Controller
import io.sn.etoile.utils.scenecontrol.controllers.IPositionController

class BeatlinesController : Controller(), IPositionController {
    override var serializedType: String = "beatlines"

    override var active: ValueChannel = 1f.const()

    override var enablePositionModule: Boolean = false
    override var translationX: ValueChannel = ConstantChannel(0f)
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var translationY: ValueChannel = ConstantChannel(0f)
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var translationZ: ValueChannel = ConstantChannel(0f)
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var rotationX: ValueChannel = ConstantChannel(0f)
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var rotationY: ValueChannel = ConstantChannel(0f)
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var rotationZ: ValueChannel = ConstantChannel(0f)
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var scaleX: ValueChannel = ConstantChannel(1f)
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var scaleY: ValueChannel = ConstantChannel(1f)
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var scaleZ: ValueChannel = ConstantChannel(1f)
        set(value) {
            field = value
            enablePositionModule = true
        }
}