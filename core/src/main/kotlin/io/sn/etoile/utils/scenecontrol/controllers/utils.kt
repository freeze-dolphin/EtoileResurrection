package io.sn.etoile.utils.scenecontrol.controllers

import io.sn.etoile.utils.scenecontrol.channels.ValueChannel

interface IController {
    var active: ValueChannel
}

interface IPositionController : IController {
    var enablePositionModule: Boolean

    var translationX: ValueChannel
    var translationY: ValueChannel
    var translationZ: ValueChannel

    var rotationX: ValueChannel
    var rotationY: ValueChannel
    var rotationZ: ValueChannel

    var scaleX: ValueChannel
    var scaleY: ValueChannel
    var scaleZ: ValueChannel
}

interface IColorController : IController {
    var enableColorModule: Boolean

    var colorR: ValueChannel
    var colorG: ValueChannel
    var colorB: ValueChannel
    var colorH: ValueChannel
    var colorS: ValueChannel
    var colorV: ValueChannel
    var colorA: ValueChannel
}

interface ILayerController : IController {
    var enableLayerModule: Boolean

    var layer: ValueChannel
    var sort: ValueChannel
    var alpha: ValueChannel
}

interface INoteGroupController : IController {
    var enableNoteGroupModule: Boolean

    var angleX: ValueChannel
    var angleY: ValueChannel

    var judgeSizeX: ValueChannel
    var judgeSizeY: ValueChannel

    var judgeOffsetX: ValueChannel
    var judgeOffsetY: ValueChannel
    var judgeOffsetZ: ValueChannel

    var rotationIndividualX: ValueChannel
    var rotationIndividualY: ValueChannel
    var rotationIndividualZ: ValueChannel

    var scaleIndividualX: ValueChannel
    var scaleIndividualY: ValueChannel
    var scaleIndividualZ: ValueChannel
}

interface ICameraController : IController {
    var enableCameraModule: Boolean

    var fieldOfView: ValueChannel
    var tiltFactor: ValueChannel
}

interface ITextureController : IController {
    var enableTextureModule: Boolean

    var textureOffsetX: ValueChannel
    var textureOffsetY: ValueChannel
    var textureScaleX: ValueChannel
    var textureScaleY: ValueChannel
}

interface ITrackController : IController {
    var enableTrackModule: Boolean

    var edgeLAlpha: ValueChannel
    var edgeRAlpha: ValueChannel

    var lane1Alpha: ValueChannel
    var lane2Alpha: ValueChannel
    var lane3Alpha: ValueChannel
    var lane4Alpha: ValueChannel
}

enum class PartSide(val value: Int, val char: Char) {
    LEFT(-1, 'l'), RIGHT(1, 'r')
}