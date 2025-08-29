package io.sn.etoile.utils.scenecontrol.channels

import io.sn.etoile.utils.scenecontrol.Union
import io.sn.etoile.utils.scenecontrol.channels.math.*
import io.sn.etoile.utils.scenecontrol.io.ISerializableUnit
import io.sn.etoile.utils.scenecontrol.io.ScenecontrolSerialization

/**
 * Assets/Scripts/Gameplay/Scenecontrol/Channels/ValueChannel.cs
 */
abstract class ValueChannel : ISerializableUnit {

    var name: String? = null

    operator fun unaryPlus(): ValueChannel = this

    operator fun unaryMinus(): NegateChannel = NegateChannel(this)

    operator fun plus(other: ValueChannel): SumChannel = SumChannel(this, other)
    operator fun plus(value: Float): SumChannel = this + ConstantChannel(value)
    operator fun plus(value: Int): SumChannel = this + value.toFloat()

    operator fun minus(other: ValueChannel): SumChannel = SumChannel(this, -other)
    operator fun minus(value: Float): SumChannel = this + (-value)
    operator fun minus(value: Int): SumChannel = this - value.toFloat()

    operator fun times(other: ValueChannel): ProductChannel = ProductChannel(this, other)
    operator fun times(value: Float): ProductChannel = this * ConstantChannel(value)
    operator fun times(value: Int): ProductChannel = this * value.toFloat()

    operator fun div(other: ValueChannel): ProductChannel = this * InverseChannel(other)
    operator fun div(value: Float): ProductChannel = this * (1f / value)
    operator fun div(value: Int): ProductChannel = this / value.toFloat()

    operator fun rem(other: ValueChannel): ModuloChannel = ModuloChannel(this, other)
    operator fun rem(value: Float): ModuloChannel = this % ConstantChannel(value)
    operator fun rem(value: Int): ModuloChannel = this % value.toFloat()

    abstract fun valueAt(timing: Long): Float

    abstract override fun serializeProperties(serialization: ScenecontrolSerialization): List<Union>?

    fun find(name: String): ValueChannel? {
        if (this.name == name) {
            return this
        }

        for (child in getChildrenChannels()) {
            val channel = child.find(name)
            if (channel != null) {
                return channel
            }
        }

        return null
    }

    protected abstract fun getChildrenChannels(): Iterable<ValueChannel>

}