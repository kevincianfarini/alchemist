package io.github.kevincianfarini.alchemist.type

import io.github.kevincianfarini.alchemist.internal.SaturatingLong
import io.github.kevincianfarini.alchemist.internal.toDecimalString
import io.github.kevincianfarini.alchemist.unit.MassUnit
import kotlin.jvm.JvmInline
import kotlin.math.roundToInt
import kotlin.text.Typography.nbsp

/**
 * Represents a measure of mass and is capable of storing ±9.2 billion kilograms at microgram precision.
 */
@JvmInline
public value class Mass internal constructor(internal val rawMicrograms: SaturatingLong) : Comparable<Mass> {

    // region SI Arithmetic

    /**
     * Returns the [Force] required to apply to this mass to achieve the specified [acceleration].
     *
     * This operation attempts to retain precision, but for sufficiently large values of either this mass or the
     * specified [acceleration], some precision may be lost.
     *
     * @throws IllegalArgumentException when [acceleration] is [infinite][isInfinite] and this mass is 0 or vice versa.
     */
    public operator fun times(acceleration: Acceleration): Force = acceleration * this

    // endregion

    // region Scalar Arithmetic

    /**
     * Returns a mass whose value is this mass value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Mass = div(scale.toLong())

    /**
     * Returns a mass whose value is this mass value divided by the specified [scale].
     *
     * @throws IllegalArgumentException when [scale] is equal to [Long.MAX_VALUE] or [Long.MIN_VALUE] and this
     * mass is [infinite][isInfinite].
     */
    public operator fun div(scale: Long): Mass = Mass(rawMicrograms / scale)

    /**
     * Returns the number that is the ratio of this and the [other] mass value.
     *
     * @throws IllegalArgumentException when both this and the [other] mass are [infinite][isInfinite].
     */
    public operator fun div(other: Mass): Double = rawMicrograms.toDouble() / other.rawMicrograms.toDouble()

    /**
     * Returns the negative of this mass value.
     */
    public operator fun unaryMinus(): Mass = Mass(-rawMicrograms)

    /**
     * Returns a mass whose value is the difference between this and the [other] mass value.
     *
     * @throws IllegalArgumentException if this mass and the [other] mass are both
     * [infinite][isInfinite] but have equivalent signs.
     */
    public operator fun minus(other: Mass): Mass = Mass(rawMicrograms - other.rawMicrograms)

    /**
     * Returns a mass whose value is the sum between this and the [other] mass value.
     *
     * @throws IllegalArgumentException if this mass and the [other] mass are both
     * [infinite][isInfinite] but have differing signs.
     */
    public operator fun plus(other: Mass): Mass = Mass(rawMicrograms + other.rawMicrograms)

    /**
     * Returns a mass whose value is multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this mass is [infinite][isInfinite] and [scale] is 0.
     */
    public operator fun times(scale: Int): Mass = times(scale.toLong())

    /**
     * Returns a mass whose value is multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this mass is [infinite][isInfinite] and [scale] is 0, or when this
     * mass is 0 and scale is [Long.MAX_VALUE] or [Long.MIN_VALUE].
     */
    public operator fun times(scale: Long): Mass = Mass(rawMicrograms * scale)

    /**
     * Returns a mass whose value is multiplied by the specified [scale]. This operation may be rounded when the result
     * cannot be precisely represented with a [Double] number.
     *
     * @throws IllegalArgumentException when this mass is [infinite][isInfinite] and [scale] is 0.0 or when this mass is 0
     * and scale is [infinite][Double.isInfinite].
     */
    public operator fun times(scale: Double): Mass = Mass(rawMicrograms * scale)

    /**
     * Returns a mass whose value is divided by the specified [scale]. This operation may be rounded when the result
     * cannot be precisely represented with a [Double] number.
     *
     * @throws IllegalArgumentException when this mass is [infinite][isInfinite] and [scale] is 0.0 or when this mass is 0
     * and scale is [infinite][Double.isInfinite].
     */
    public operator fun div(scale: Double): Mass = Mass(rawMicrograms / scale)

    // endregion

    // region Mass to Scalar Conversions

    /**
     * Splits this mass into teragrams, gigagrams, megagrams, kilograms, grams, milligrams, and micrograms and
     * executes the [action] with those components. The result of [action] is returned as the result of this function.
     *
     * Infinite mass values invoke [action] with [Long.MAX_VALUE] or [Long.MIN_VALUE] for every component, depending
     * on the infinite value's sign.
     */
    public fun <T> toInternationalComponents(
        action: (
            teragrams: Long,
            gigagrams: Long,
            megagrams: Long,
            kilograms: Long,
            grams: Long,
            milligrams: Long,
            micrograms: Long,
        ) -> T
    ): T {
        val tera = rawMicrograms / MassUnit.International.Teragram.microgramScale
        val teraRemainder = rawMicrograms % MassUnit.International.Teragram.microgramScale
        val giga = teraRemainder / MassUnit.International.Gigagram.microgramScale
        val gigaRemainder = teraRemainder % MassUnit.International.Gigagram.microgramScale
        val mega = gigaRemainder / MassUnit.International.Megagram.microgramScale
        val megaRemainder = gigaRemainder % MassUnit.International.Megagram.microgramScale
        val kilo = megaRemainder / MassUnit.International.Kilogram.microgramScale
        val kiloRemainder = megaRemainder % MassUnit.International.Kilogram.microgramScale
        val grams = kiloRemainder / MassUnit.International.Gram.microgramScale
        val gramRemainder = kiloRemainder % MassUnit.International.Gram.microgramScale
        val milli = gramRemainder / MassUnit.International.Milligram.microgramScale
        val micro = gramRemainder % MassUnit.International.Megagram.microgramScale
        return action(
            tera.rawValue,
            giga.rawValue,
            mega.rawValue,
            kilo.rawValue,
            grams.rawValue,
            milli.rawValue,
            micro.rawValue
        )
    }

    /**
     * Returns the value of this mass expressed as a [Long] number of the specified [unit]. Infinite values are
     * converted to either [Long.MAX_VALUE] or [Long.MIN_VALUE] depending on its sign.
     */
    public fun toLong(unit: MassUnit): Long {
        return (rawMicrograms / unit.microgramScale).rawValue
    }

    /**
     * Returns the value of this mass expressed as a [Double] number of the specified [unit]. Infinite values are
     * converted to either [Double.POSITIVE_INFINITY] or [Double.NEGATIVE_INFINITY] depending on its sign.
     */
    public fun toDouble(unit: MassUnit): Double {
        return rawMicrograms.toDouble() / unit.microgramScale.toDouble()
    }

    /**
     * Returns a fractional string representation of this mass expressed in the specified [unit] and is rounded
     * to the specified [decimals].
     */
    public fun toString(unit: MassUnit, decimals: Int = 0): String = when (isInfinite()) {
        true -> rawMicrograms.toString()
        false -> buildString {
            append(toDouble(unit).toDecimalString(decimals))
            append(nbsp)
            append(unit.symbol)
        }
    }

    /**
     * Returns a fractional string representation of this mass expressed in the largest [MassUnit.International]
     * quantity which is greater than or equal to 1.
     */
    override fun toString(): String {
        val largestUnit = MassUnit.International.entries.asReversed().firstOrNull { unit ->
            rawMicrograms.absoluteValue / unit.microgramScale > 0
        }
        return toString(largestUnit ?: MassUnit.International.Microgram, decimals = 2)
    }

    // endregion

    // region Comparisons

    /**
     * Returns true if this area value is infinite.
     */
    public fun isInfinite(): Boolean = rawMicrograms.isInfinite()

    /**
     * Returns true if this area value is finite.
     */
    public fun isFinite(): Boolean = rawMicrograms.isFinite()

    /**
     * Compares this mass with the [other] mass. Returns zero if this mass is equal
     * to the specified [other] mass, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: Mass): Int = rawMicrograms.compareTo(other.rawMicrograms)

    // endregion
}

