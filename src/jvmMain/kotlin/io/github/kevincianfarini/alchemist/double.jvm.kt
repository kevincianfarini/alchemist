package io.github.kevincianfarini.alchemist

import java.math.RoundingMode
import java.text.DecimalFormat

internal actual fun Double.toDecimalString(decimals: Int): String {
    val format = DecimalFormat("0").apply {
        if (decimals > 0) {
            minimumFractionDigits = decimals
            maximumFractionDigits = decimals
        }
        roundingMode = RoundingMode.HALF_UP
    }
    return format.format(this)
}