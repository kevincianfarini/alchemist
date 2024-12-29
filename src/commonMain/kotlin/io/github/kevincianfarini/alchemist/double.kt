package io.github.kevincianfarini.alchemist

/**
 * Returns a decimal string that has been rounded to the specified number of [decimals].
 */
internal expect fun Double.toDecimalString(decimals: Int): String