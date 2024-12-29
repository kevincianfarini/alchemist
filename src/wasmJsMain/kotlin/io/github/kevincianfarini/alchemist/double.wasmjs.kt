package io.github.kevincianfarini.alchemist

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sign
import kotlin.math.ceil
import kotlin.math.log10

internal actual fun Double.toDecimalString(decimals: Int): String {
    val rounded = if (decimals == 0) {
        this
    } else {
        val pow = (10.0).pow(decimals)
        round(abs(this) * pow) / pow * sign(this)
    }
    return if (abs(rounded) < 1e21) {
        // toFixed switches to scientific format after 1e21
        toFixed(rounded, decimals)
    } else {
        // toPrecision outputs the specified number of digits, but only for positive numbers
        val positive = abs(rounded)
        val positiveString = toPrecision(positive, ceil(log10(positive)).toInt() + decimals)
        if (rounded < 0) "-$positiveString" else positiveString
    }
}

@Suppress("UNUSED_PARAMETER")
private fun toFixed(value: Double, decimals: Int): String =
    js("value.toFixed(decimals)")

@Suppress("UNUSED_PARAMETER")
private fun toPrecision(value: Double, decimals: Int): String =
    js("value.toPrecision(decimals)")
