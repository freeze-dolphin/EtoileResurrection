package io.sn.etoile.utils.scenecontrol.controllers.internal

import io.sn.etoile.utils.scenecontrol.channels.ValueChannel
import io.sn.etoile.utils.scenecontrol.channels.math.ConstantChannel
import io.sn.etoile.utils.scenecontrol.channels.math.const
import io.sn.etoile.utils.scenecontrol.controllers.Controller
import io.sn.etoile.utils.scenecontrol.controllers.IColorController
import io.sn.etoile.utils.scenecontrol.controllers.INoteGroupController
import io.sn.etoile.utils.scenecontrol.controllers.IPositionController

class NoteGroupController(override var serializedType: String) :
    Controller(),
    IPositionController,
    INoteGroupController,
    IColorController {
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
    override var scaleZ: ValueChannel = ConstantChannel(0f)
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
    override var colorV: ValueChannel = ConstantChannel(0f)
        set(value) {
            field = value
            enableColorModule = true
        }
    override var colorA: ValueChannel = ConstantChannel(255f)
        set(value) {
            field = value
            enableColorModule = true
        }

    override var enableNoteGroupModule: Boolean = false
    override var angleX: ValueChannel = 0f.const()
        set(value) {
            field = value
            enableNoteGroupModule = true
        }
    override var angleY: ValueChannel = 0f.const()
        set(value) {
            field = value
            enableNoteGroupModule = true
        }
    override var judgeSizeX: ValueChannel = 1f.const()
        set(value) {
            field = value
            enableNoteGroupModule = true
        }
    override var judgeSizeY: ValueChannel = 1f.const()
        set(value) {
            field = value
            enableNoteGroupModule = true
        }
    override var judgeOffsetX: ValueChannel = 0f.const()
        set(value) {
            field = value
            enableNoteGroupModule = true
        }
    override var judgeOffsetY: ValueChannel = 0f.const()
        set(value) {
            field = value
            enableNoteGroupModule = true
        }
    override var judgeOffsetZ: ValueChannel = 0f.const()
        set(value) {
            field = value
            enableNoteGroupModule = true
        }
    override var rotationIndividualX: ValueChannel = 0f.const()
        set(value) {
            field = value
            enableNoteGroupModule = true
        }
    override var rotationIndividualY: ValueChannel = 0f.const()
        set(value) {
            field = value
            enableNoteGroupModule = true
        }
    override var rotationIndividualZ: ValueChannel = 0f.const()
        set(value) {
            field = value
            enableNoteGroupModule = true
        }
    override var scaleIndividualX: ValueChannel = 1f.const()
        set(value) {
            field = value
            enableNoteGroupModule = true
        }
    override var scaleIndividualY: ValueChannel = 1f.const()
        set(value) {
            field = value
            enableNoteGroupModule = true
        }
    override var scaleIndividualZ: ValueChannel = 1f.const()
        set(value) {
            field = value
            enableNoteGroupModule = true
        }

}