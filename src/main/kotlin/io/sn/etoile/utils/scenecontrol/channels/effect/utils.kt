package io.sn.etoile.utils.scenecontrol.channels.effect

import io.sn.aetherium.utils.EasingFunction
import io.sn.aetherium.utils.linear

val constant: EasingFunction = { 0.0 }

private val easingMapping: Map<EasingFunction, String> = mapOf(
    linear to "l",
    constant to "cnsti"
)

fun getEasingString(easing: EasingFunction): String {
    return easingMapping[easing] ?: "l"
}
