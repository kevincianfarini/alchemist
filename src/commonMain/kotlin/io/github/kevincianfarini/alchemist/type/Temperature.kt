package io.github.kevincianfarini.alchemist.type

import io.github.kevincianfarini.alchemist.internal.SaturatingLong
import io.github.kevincianfarini.alchemist.internal.toDecimalString
import io.github.kevincianfarini.alchemist.unit.TemperatureUnit
import io.github.kevincianfarini.alchemist.unit.convertNanokelvinsToThis
import kotlin.jvm.JvmInline
import kotlin.math.roundToLong

/**
 * Represents a temperature and is capable of storing ±9.2 billion Kelvin (±9.2 billion °C, ±16.6 billion °F) at
 * nanokelvin precision.
 */
@JvmInline
public value class Temperature internal constructor(private val rawNanokelvin: SaturatingLong) : Comparable<Temperature> {

    // region Scalar Arithmetic

    /**
     * Returns the number that is the ratio of this and the [other] temperature value.
     *
     * @throws IllegalArgumentException when both this and the [other] temperature are [infinite][isInfinite].
     */
    public operator fun div(other: Temperature): Double {
        return rawNanokelvin.toDouble() / other.rawNanokelvin.toDouble()
    }

    /**
     * Returns a temperature whose value is this temperature value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Temperature = div(scale.toLong())

    /**
     * Returns a temperature whose value is this temperature value divided by the specified [scale].
     *
     * @throws IllegalArgumentException when [scale] is equal to [Long.MAX_VALUE] or [Long.MIN_VALUE] and this
     * temperature is [infinite][isInfinite].
     */
    public operator fun div(scale: Long): Temperature = Temperature(rawNanokelvin / scale)

    /**
     * Returns the negative of this temperature value.
     */
    public operator fun unaryMinus(): Temperature = Temperature(-rawNanokelvin)

    /**
     * Returns a temperature whose value is the difference between this and the [other] temperature value.
     *
     * @throws IllegalArgumentException if this temperature and the [other] temperature are both
     * [infinite][isInfinite] but have equivalent signs.
     */
    public operator fun minus(other: Temperature): Temperature = Temperature(rawNanokelvin - other.rawNanokelvin)

    /**
     * Returns a temperature whose value is the sum between this and the [other] temperature value.
     *
     * @throws IllegalArgumentException if this temperature and the [other] temperature are both
     * [infinite][isInfinite] but have differing signs.
     */
    public operator fun plus(other: Temperature): Temperature = Temperature(rawNanokelvin + other.rawNanokelvin)

    /**
     * Returns a temperature whose value is multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this temperature is [infinite][isInfinite] and [scale] is 0.
     */
    public operator fun times(scale: Int): Temperature = times(scale.toLong())

    /**
     * Returns a temperature whose value is multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this temperature is [infinite][isInfinite] and [scale] is 0, or when this
     * temperature is 0 and scale is [Long.MAX_VALUE] or [Long.MIN_VALUE].
     */
    public operator fun times(scale: Long): Temperature = Temperature(rawNanokelvin * scale)

    // endregion

    // region Temperature to Scalar Conversions

    /**
     * Returns the value of this temperature expressed as a [Long] number of the specified [unit]. Infinite values are
     * converted to either [Long.MAX_VALUE] or [Long.MIN_VALUE] depending on its sign.
     */
    public fun toLong(unit: TemperatureUnit): Long = toDouble(unit).roundToLong()

    /**
     * Returns the value of this temperature expressed as a [Double] number of the specified [unit]. Infinite values are
     * converted to either [Double.POSITIVE_INFINITY] or [Double.NEGATIVE_INFINITY] depending on its sign.
     */
    public fun toDouble(unit: TemperatureUnit): Double = unit.convertNanokelvinsToThis(rawNanokelvin)

    /**
     * Returns a fractional string representation of this temperature expressed in the specified [unit] and is rounded
     * to the specified [decimals].
     */
    public fun toString(unit: TemperatureUnit, decimals: Int = 0): String {
        return buildString {
            append(unit.convertNanokelvinsToThis(rawNanokelvin).toDecimalString(decimals))
            append(unit.symbol)
        }
    }

    /**
     * Returns a fractional string representation of this temperature expressed in the largest
     * [TemperatureUnit.International] quantity which is greater than or equal to 1.
     */
    public override fun toString(): String {
        return toString(toStringUnit(), decimals = 2)
    }

    private fun toStringUnit(): TemperatureUnit = when {
        TemperatureUnit.International.Gigakelvin.convertNanokelvinsToThis(rawNanokelvin) >= 1.0 -> {
            TemperatureUnit.International.Gigakelvin
        }
        TemperatureUnit.International.Megakelvin.convertNanokelvinsToThis(rawNanokelvin) >= 1.0 -> {
            TemperatureUnit.International.Megakelvin
        }
        TemperatureUnit.International.Kilokelvin.convertNanokelvinsToThis(rawNanokelvin) >= 1.0 -> {
            TemperatureUnit.International.Kilokelvin
        }
        TemperatureUnit.International.Kelvin.convertNanokelvinsToThis(rawNanokelvin) >= 1.0 -> {
            TemperatureUnit.International.Kelvin
        }
        TemperatureUnit.International.Millikelvin.convertNanokelvinsToThis(rawNanokelvin) >= 1.0 -> {
            TemperatureUnit.International.Millikelvin
        }
        TemperatureUnit.International.Microkelvin.convertNanokelvinsToThis(rawNanokelvin) >= 1.0 -> {
            TemperatureUnit.International.Microkelvin
        }
        else -> TemperatureUnit.International.Nanokelvin
    }

    // endregion

    // region Comparisons

    /**
     * Returns true if this area value is infinite.
     */
    public fun isInfinite(): Boolean = rawNanokelvin.isInfinite()

    /**
     * Returns true if this area value is finite.
     */
    public fun isFinite(): Boolean = rawNanokelvin.isFinite()

    /**
     * Compares this temperature with the [other] temperature. Returns zero if this temperature is equal
     * to the specified [other] temperature, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    public override fun compareTo(other: Temperature): Int {
        return rawNanokelvin.compareTo(other.rawNanokelvin)
    }

    // endregion
}
