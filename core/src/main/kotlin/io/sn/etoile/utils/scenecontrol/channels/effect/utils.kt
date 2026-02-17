package io.sn.etoile.utils.scenecontrol.channels.effect

import io.sn.aetherium.utils.EaseFunc
import io.sn.aetherium.utils.linear

val constant: EaseFunc = { 0.0 }

private val easingMapping: Map<EaseFunc, String> = mapOf(
    linear to "l",
    constant to "cnsti"
)

fun getEasingString(easing: EaseFunc): String {
    return easingMapping[easing] ?: "l"
}
