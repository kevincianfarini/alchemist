package io.github.kevincianfarini.alchemist.type

import io.github.kevincianfarini.alchemist.internal.SaturatingLong
import io.github.kevincianfarini.alchemist.internal.secondScale
import io.github.kevincianfarini.alchemist.internal.shortNameSquared
import io.github.kevincianfarini.alchemist.internal.toDecimalString
import io.github.kevincianfarini.alchemist.scalar.nmPerSecond
import io.github.kevincianfarini.alchemist.unit.LengthUnit
import kotlin.jvm.JvmInline
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.text.Typography.nbsp
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * Represents a measure of acceleration and is capable of storing ±9.2 billion m/s² at nm/s² precision.
 */
@JvmInline
public value class Acceleration internal constructor(
    private val rawNanometersPerSecondSquared: SaturatingLong
) : Comparable<Acceleration> {

    // region SI Arithmetic

    /**
     * Returns the resulting [Velocity] after multiplying this acceleration by the specified [duration].
     *
     * This operation attempts to retain precision, but for sufficiently large values of this acceleration or the
     * specified [duration] some precision may be lost.
     *
     * @throws IllegalArgumentException if this acceleration is infinite and [duration] is zero, or if this acceleration
     * is zero and [duration] is infinite.
     */
    public operator fun times(duration: Duration): Velocity = duration.toComponents { seconds, nanoseconds ->
        val secondComponent = (rawNanometersPerSecondSquared * seconds).nmPerSecond
        val preciseNanosecondComponent = ((rawNanometersPerSecondSquared * nanoseconds) / 1_000_000_000).nmPerSecond
        if (preciseNanosecondComponent.isFinite()) {
            secondComponent + preciseNanosecondComponent
        } else {
            val coarseNanosecondComponent = ((rawNanometersPerSecondSquared / 1_000_000_000) * nanoseconds).nmPerSecond
            secondComponent + coarseNanosecondComponent
        }
    }

    /**
     * Returns the resulting [Force] after multiplying this acceleration by the specified [mass].
     *
     * This operation attempts to retain precision, but for sufficiently large values of this acceleration or the
     * specified [mass] some precision may be lost.
     *
     * @throws IllegalArgumentException if this acceleration is infinite and [mass] is zero, or if this acceleration
     * is zero and [mass] is infinite.
     */
    public operator fun times(mass: Mass): Force {
        return mass.toInternationalComponents { tera, giga, mega, kilo, grams, milli, micro ->
            // Try to find the right level which we can perform this operation at without losing precision.
            // --------------------------------------------------------------------------------------------
            // 1 nm/s² * 1 microgram is 1 attonewton.
            // 1 nm/s² * 1 milligram is 1 femtonewton.
            // 1 nm/s² * 1 gram is 1 piconewton.
            // 1 nm/s² * 1 kilogram is 1 nanonewton.
            // 1 nm/s² * 1 megagram is 1 micronewton.
            // 1 nm/s² * 1 gigagram is 1 millinewton.
            // 1 nm/s² * 1 teragram is 1 newton.
            // --------------------------------------------------------------------------------------------
            val newtons = rawNanometersPerSecondSquared * tera
            val millinewtons = rawNanometersPerSecondSquared * giga
            val micronewtons = rawNanometersPerSecondSquared * mega
            val nanonewtons = rawNanometersPerSecondSquared * kilo
            val piconewtons = rawNanometersPerSecondSquared * grams
            val femtonewtons = rawNanometersPerSecondSquared * milli
            val attonewtons = rawNanometersPerSecondSquared * micro
            // ----------- Try attonewton precision. ------------------------------------------------------
            val attoN = attonewtons + (femtonewtons * 1_000) + (piconewtons * 1_000_000) + (nanonewtons * 1_000_000_000) + (micronewtons * 1_000_000_000_000) + (millinewtons * 1_000_000_000_000_000) + (newtons * 1_000_000_000_000_000_000)
            if (attoN.isFinite()) return@toInternationalComponents Force(attoN / 1_000_000_000)
            // ----------- Try femtonewton precision. ------------------------------------------------------
            val femtoN = (attonewtons / 1_000) + femtonewtons + (piconewtons * 1_000) + (nanonewtons * 1_000_000) + (micronewtons * 1_000_000_000) + (millinewtons * 1_000_000_000_000) + (newtons * 1_000_000_000_000_000)
            if (femtoN.isFinite()) return@toInternationalComponents Force(femtoN / 1_000_000)
            // ----------- Try piconewton precision. ------------------------------------------------------
            val picoN = (attonewtons / 1_000_000) + (femtonewtons / 1_000) + piconewtons + (nanonewtons * 1_000) + (micronewtons * 1_000_000) + (millinewtons * 1_000_000_000) + (newtons * 1_000_000_000_000)
            if (picoN.isFinite()) return@toInternationalComponents Force(picoN / 1_000)
            // ----------- Default nanonewton precision. --------------------------------------------------
            val nanoN = (attonewtons / 1_000_000_000) + (femtonewtons / 1_000_000) + (piconewtons / 1_000) + nanonewtons + (micronewtons * 1_000) + (millinewtons * 1_000_000) + (newtons * 1_000_000_000)
            Force(nanoN)
        }
    }

    // endregion

    // region Scalar Arithmetic

    /**
     * Returns the number that is the ratio of this and the [other] acceleration value.
     *
     * @throws IllegalArgumentException when both this and the [other] acceleration are [infinite][isInfinite].
     */
    public operator fun div(other: Acceleration): Double {
        return rawNanometersPerSecondSquared.toDouble() / other.rawNanometersPerSecondSquared.toDouble()
    }

    /**
     * Returns an acceleration whose value is this acceleration value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Acceleration = div(scale.toLong())

    /**
     * Returns an acceleration whose value is this acceleration value divided by the specified [scale].
     *
     * @throws IllegalArgumentException when [scale] is equal to [Long.MAX_VALUE] or [Long.MIN_VALUE] and this
     * acceleration is [infinite][isInfinite].
     */
    public operator fun div(scale: Long): Acceleration = Acceleration(rawNanometersPerSecondSquared / scale)

    /**
     * Returns the negative of this acceleration value.
     */
    public operator fun unaryMinus(): Acceleration = Acceleration(-rawNanometersPerSecondSquared)

    /**
     * Returns an acceleration whose value is the difference between this and the [other] acceleration value.
     *
     * @throws IllegalArgumentException if this acceleration and the [other] acceleration are both
     * [infinite][isInfinite] but have equivalent signs.
     */
    public operator fun minus(other: Acceleration): Acceleration {
        return Acceleration(rawNanometersPerSecondSquared - other.rawNanometersPerSecondSquared)
    }

    /**
     * Returns an acceleration whose value is the sum between this and the [other] acceleration value.
     *
     * @throws IllegalArgumentException if this acceleration and the [other] acceleration are both
     * [infinite][isInfinite] but have differing signs.
     */
    public operator fun plus(other: Acceleration): Acceleration {
        return Acceleration(rawNanometersPerSecondSquared + other.rawNanometersPerSecondSquared)
    }

    /**
     * Returns an acceleration whose value is multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this acceleration is [infinite][isInfinite] and [scale] is 0.
     */
    public operator fun times(scale: Int): Acceleration = times(scale.toLong())

    /**
     * Returns an acceleration whose value is multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this acceleration is [infinite][isInfinite] and [scale] is 0, or when this
     * acceleration is 0 and scale is [Long.MAX_VALUE] or [Long.MIN_VALUE].
     */
    public operator fun times(scale: Long): Acceleration = Acceleration(rawNanometersPerSecondSquared * scale)

    /**
     * Returns an acceleration whose value is multiplied by the specified [scale]. This operation may be rounded when the result
     * cannot be precisely represented with a [Double] number.
     *
     * @throws IllegalArgumentException when this acceleration is [infinite][isInfinite] and [scale] is 0.0 or when this acceleration is 0
     * and scale is [infinite][Double.isInfinite].
     */
    public operator fun times(scale: Double): Acceleration = Acceleration(rawNanometersPerSecondSquared * scale)

    /**
     * Returns an acceleration whose value is divided by the specified [scale]. This operation may be rounded when the result
     * cannot be precisely represented with a [Double] number.
     *
     * @throws IllegalArgumentException when this acceleration is [infinite][isInfinite] and [scale] is 0.0 or when this acceleration is 0
     * and scale is [infinite][Double.isInfinite].
     */
    public operator fun div(scale: Double): Acceleration = Acceleration(rawNanometersPerSecondSquared / scale)

    // endregion

    // region Acceleration to Scalar Conversions

    /**
     * Returns the value of this acceleration expressed as a [Long] number of the specified [lengthUnit] per
     * [durationUnit]². Infinite values are converted to either [Long.MAX_VALUE] or [Long.MIN_VALUE] depending on its
     * sign.
     */
    public fun toLong(lengthUnit: LengthUnit, durationUnit: DurationUnit): Long {
        return toDouble(lengthUnit, durationUnit).roundToLong()
    }

    /**
     * Returns the value of this acceleration expressed as a [Double] number of the specified [lengthUnit] per
     * [durationUnit]². Infinite values are converted to either [Double.POSITIVE_INFINITY] or [Double.NEGATIVE_INFINITY]
     * depending on its sign.
     */
    public fun toDouble(lengthUnit: LengthUnit, durationUnit: DurationUnit): Double {
        val lengthPerSecond2 = rawNanometersPerSecondSquared.toDouble() / lengthUnit.nanometerScale.toDouble()
        return lengthPerSecond2 * durationUnit.secondScale * durationUnit.secondScale
    }

    /**
     * Returns a fractional string representation of this acceleration expressed in the specified [lengthUnit] per
     * [durationUnit]² and is rounded to the specified [decimals].
     */
    public fun toString(lengthUnit: LengthUnit, durationUnit: DurationUnit, decimals: Int = 0): String {
        return when (isInfinite()) {
            true -> rawNanometersPerSecondSquared.toString()
            false -> buildString {
                append(toDouble(lengthUnit, durationUnit).toDecimalString(decimals))
                append(nbsp)
                append(lengthUnit.symbol)
                append("/")
                append(durationUnit.shortNameSquared)
            }
        }
    }

    /**
     * Returns a fractional string representation of this acceleration expressed in the largest
     * [LengthUnit.International] per second² quantity which is greater than or equal to 1.
     */
    override fun toString(): String {
        val lengthUnit = LengthUnit.International.entries.asReversed().firstOrNull { unit ->
            rawNanometersPerSecondSquared.absoluteValue / unit.nanometerScale > 0
        }
        return toString(lengthUnit ?: LengthUnit.International.Nanometer, DurationUnit.SECONDS, decimals = 2)
    }

    // endregion

    // region Comparisons

    /**
     * Returns true if this acceleration value is finite.
     */
    public fun isFinite(): Boolean = rawNanometersPerSecondSquared.isFinite()

    /**
     * Returns true if this acceleration value is infinite.
     */
    public fun isInfinite(): Boolean = rawNanometersPerSecondSquared.isInfinite()

    /**
     * Compares this acceleration with the [other] acceleration. Returns zero if this acceleration is equal
     * to the specified [other] acceleration, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: Acceleration): Int {
        return rawNanometersPerSecondSquared.compareTo(other.rawNanometersPerSecondSquared)
    }
    // endregion
}
