package io.sn.etoile.utils.scenecontrol.controllers.internal

import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.channels.math.const
import io.sn.etoile.utils.scenecontrol.controllers.common.SpriteController

class DarkenController : SpriteController("darken") {
    override var active: ValueChannel = 0f.const()

    override var colorR: ValueChannel = 0f.const()
        set(value) {
            field = value
            enableColorModule = true
        }
    override var colorG: ValueChannel = 0f.const()
        set(value) {
            field = value
            enableColorModule = true
        }
    override var colorB: ValueChannel = 0f.const()
        set(value) {
            field = value
            enableColorModule = true
        }
    override var colorA: ValueChannel = 255f.const()
        set(value) {
            field = value
            enableColorModule = true
        }
    override var colorV: ValueChannel = 0f.const()
        set(value) {
            field = value
            enableColorModule = true
        }

    override var scaleX: ValueChannel = 180f.const()
        set(value) {
            field = value
            enablePositionModule = true
        }
    override var scaleY: ValueChannel = 180f.const()
        set(value) {
            field = value
            enablePositionModule = true
        }
}