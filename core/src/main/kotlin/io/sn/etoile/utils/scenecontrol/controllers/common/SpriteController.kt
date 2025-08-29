package io.sn.etoile.utils.scenecontrol.controllers.common

import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.channels.math.ConstantChannel
import io.sn.etoile.utils.scenecontrol.controllers.*

abstract class SpriteController(
    override var serializedType: String,
) : Controller(),
    IPositionController,
    ILayerController,
    ITextureController,
    IColorController {
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

    override var enableColorModule: Boolean = false
    override var colorR: ValueChannel = ConstantChannel(255f)
        set(value) {
            field = value
            enableColorModule = true
        }
    override var colorG: ValueChannel = ConstantChannel(255f)
        set(value) {
            field = value
            enableColorModule = true
        }
    override var colorB: ValueChannel = ConstantChannel(255f)
        set(value) {
            field = value
            enableColorModule = true
        }
    override var colorH: ValueChannel = ConstantChannel(0f)
        set(value) {
            field = value
            enableColorModule = true
        }
    override var colorS: ValueChannel = ConstantChannel(0f)
        set(value) {
            field = value
            enableColorModule = true
        }
    override var colorV: ValueChannel = ConstantChannel(100f)
        set(value) {
            field = value
            enableColorModule = true
        }
    override var colorA: ValueChannel = ConstantChannel(255f)
        set(value) {
            field = value
            enableColorModule = true
        }

    override var enableLayerModule: Boolean = false
    override var layer: ValueChannel = ConstantChannel(0f)
        set(value) {
            field = value
            enableLayerModule = true
        }
    override var sort: ValueChannel = ConstantChannel(2f)
        set(value) {
            field = value
            enableLayerModule = true
        }
    override var alpha: ValueChannel = ConstantChannel(255f)
        set(value) {
            field = value
            enableLayerModule = true
        }

    override var enableTextureModule: Boolean = false
    override var textureOffsetX: ValueChannel = ConstantChannel(0f)
        set(value) {
            field = value
            enableTextureModule = true
        }
    override var textureOffsetY: ValueChannel = ConstantChannel(0f)
        set(value) {
            field = value
            enableTextureModule = true
        }
    override var textureScaleX: ValueChannel = ConstantChannel(1f)
        set(value) {
            field = value
            enableTextureModule = true
        }
    override var textureScaleY: ValueChannel = ConstantChannel(1f)
        set(value) {
            field = value
            enableTextureModule = true
        }
}


