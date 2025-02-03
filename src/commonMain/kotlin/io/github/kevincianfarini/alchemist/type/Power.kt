package io.github.kevincianfarini.alchemist.type

import io.github.kevincianfarini.alchemist.internal.POSITIVE_INFINITY
import io.github.kevincianfarini.alchemist.internal.SaturatingLong
import io.github.kevincianfarini.alchemist.internal.sign
import io.github.kevincianfarini.alchemist.internal.toDecimalComponents
import io.github.kevincianfarini.alchemist.internal.toDecimalString
import io.github.kevincianfarini.alchemist.scalar.microwatts
import io.github.kevincianfarini.alchemist.unit.PowerUnit
import kotlin.jvm.JvmInline
import kotlin.math.roundToInt
import kotlin.time.Duration

/**
 * Represents an amount of power and is capable of storing ±9.22 terawatts at microwatt precision.
 */
@JvmInline
public value class Power internal constructor(private val rawMicrowatts: SaturatingLong) : Comparable<Power> {

    // region SI Arithmetic

    /**
     * Returns the resulting [Energy] from applying this power over the specified [duration].
     *
     * This operation attempts to retain precision, but for sufficiently large values of either this power or [duration],
     * some precision may be lost.
     *
     * @throws IllegalArgumentException if this power is infinite and duration is zero, or if this power is zero and
     * duration is infinite.
     */
    public operator fun times(duration: Duration): Energy {
        return when {
            duration.isInfinite() || rawMicrowatts.isInfinite() -> {
                Energy(POSITIVE_INFINITY * duration.sign * rawMicrowatts)
            }
            else -> duration.toDecimalComponents { kiloseconds, seconds, millis, micros, nanos ->
                // Try to find the right level which we can perform this operation at without losing precision.
                // --------------------------------------------------------------------------------------------
                // 1 microwatt * 1 nanosecond is 1 femtojoule.
                // 1 microwatt * 1 microsecond is 1 picojoule.
                // 1 microwatt * 1 millisecond is 1 nanojoule.
                // 1 microwatt * 1 second is 1 microjoule.
                // 1 microwatt * 1,000 seconds is 1 millijoule.
                // --------------------------------------------------------------------------------------------
                val millijoules = rawMicrowatts * kiloseconds
                val microjoules = rawMicrowatts * seconds
                val nanojoules = rawMicrowatts * millis
                val picojoules = rawMicrowatts * micros
                val femtojoules = rawMicrowatts * nanos
                // ----------- Try femtojoule precision. ------------------------------------------------------
                val femtoJ = femtojoules + (picojoules * 1_000) + (nanojoules * 1_000_000) + (microjoules * 1_000_000_000) + (millijoules * 1_000_000_000_000)
                if (femtoJ.isFinite()) return@toDecimalComponents Energy(femtoJ / 1_000_000_000_000)
                // ----------- Try picojoule precision. -------------------------------------------------------
                val picoJ = (femtojoules / 1_000) + picojoules + (nanojoules * 1_000) + (microjoules * 1_000_000) + (millijoules * 1_000_000_000)
                if (picoJ.isFinite()) return@toDecimalComponents Energy(picoJ / 1_000_000_000)
                // ----------- Try nanojoule precision. -------------------------------------------------------
                val nanoJ = (femtojoules / 1_000_000) + (picojoules / 1_000) + nanojoules + (microjoules * 1_000) + (millijoules * 1_000_000)
                if (nanoJ.isFinite()) return@toDecimalComponents Energy(nanoJ / 1_000_000)
                // ----------- Try microjoule precision. -------------------------------------------------------
                val microJ = (femtojoules / 1_000_000_000) + (picojoules / 1_000_000) + (nanojoules / 1_000) + microjoules + (millijoules * 1_000)
                if (microJ.isFinite()) return@toDecimalComponents Energy(microJ / 1_000)
                // ----------- Default microjoule precision. ---------------------------------------------------
                val milliJ = (femtojoules / 1_000_000_000_000) + (picojoules / 1_000_000_000) + (nanojoules / 1_000_000) + (microjoules / 1_000) + millijoules
                Energy(milliJ)
            }
        }
    }

    // endregion

    // region Scalar Arithmetic

    /**
     * Returns the number that is the ratio of this and the [other] power value.
     *
     * @throws IllegalArgumentException when both this and the [other] power are [infinite][isInfinite].
     */
    public operator fun div(other: Power): Double {
        return rawMicrowatts.toDouble() / other.rawMicrowatts.toDouble()
    }

    /**
     * Returns a power whose value is this power value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Power = div(scale.toLong())

    /**
     * Returns a power whose value is this power value divided by the specified [scale].
     *
     * @throws IllegalArgumentException when [scale] is equal to [Long.MAX_VALUE] or [Long.MIN_VALUE] and this
     * power is [infinite][isInfinite].
     */
    public operator fun div(scale: Long): Power = Power(rawMicrowatts / scale)

    /**
     * Returns the negative of this power value.
     */
    public operator fun unaryMinus(): Power = Power(-rawMicrowatts)

    /**
     * Returns a power whose value is the difference between this and the [other] power value.
     *
     * @throws IllegalArgumentException if this power and the [other] power are both
     * [infinite][isInfinite] but have equivalent signs.
     */
    public operator fun minus(other: Power): Power = Power(rawMicrowatts - other.rawMicrowatts)

    /**
     * Returns a power whose value is the sum between this and the [other] power value.
     *
     * @throws IllegalArgumentException if this power and the [other] power are both
     * [infinite][isInfinite] but have differing signs.
     */
    public operator fun plus(other: Power): Power = Power(rawMicrowatts + other.rawMicrowatts)

    /**
     * Returns a power whose value is this power multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this power is [infinite][isInfinite] and [scale] is 0.
     */
    public operator fun times(scale: Int): Power = times(scale.toLong())

    /**
     * Returns a power whose value is this power multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this power is [infinite][isInfinite] and [scale] is 0, or when this
     * power is 0 and scale is [Long.MAX_VALUE] or [Long.MIN_VALUE].
     */
    public operator fun times(scale: Long): Power = Power(rawMicrowatts * scale)

    /**
     * Returns a power whose value is multiplied by the specified [scale]. This operation may be rounded when the result
     * cannot be precisely represented with a [Double] number.
     *
     * @throws IllegalArgumentException when this power is [infinite][isInfinite] and [scale] is 0.0 or when this power is 0
     * and scale is [infinite][Double.isInfinite].
     */
    public operator fun times(scale: Double): Power = Power(rawMicrowatts * scale)

    /**
     * Returns a power whose value is divided by the specified [scale]. This operation may be rounded when the result
     * cannot be precisely represented with a [Double] number.
     *
     * @throws IllegalArgumentException when this power is [infinite][isInfinite] and [scale] is 0.0 or when this power is 0
     * and scale is [infinite][Double.isInfinite].
     */
    public operator fun div(scale: Double): Power = Power(rawMicrowatts / scale)

    // endregion

    // region Power to Scalar Conversions

    /**
     * Returns the value of this power expressed as a [Long] number of the specified [unit]. Infinite values are
     * converted to either [Long.MAX_VALUE] or [Long.MIN_VALUE] depending on its sign.
     */
    public fun toLong(unit: PowerUnit): Long {
        return (rawMicrowatts / unit.microwattScale).rawValue
    }

    /**
     * Returns the value of this power expressed as a [Double] number of the specified [unit]. Infinite values are
     * converted to either [Double.POSITIVE_INFINITY] or [Double.NEGATIVE_INFINITY] depending on its sign.
     */
    public fun toDouble(unit: PowerUnit): Double {
        return this / unit.microwattScale.microwatts
    }

    /**
     * Returns a fractional string representation of this power expressed in the specified [unit] and is rounded
     * to the specified [decimals].
     */
    public fun toString(unit: PowerUnit, decimals: Int = 0): String = when (rawMicrowatts.isInfinite()) {
        true -> rawMicrowatts.toString()
        false -> buildString {
            append(toDouble(unit).toDecimalString(decimals))
            append(unit.symbol)
        }
    }

    /**
     * Returns a fractional string representation of this power expressed in the largest [PowerUnit.International]
     * quantity which is greater than or equal to 1.
     */
    override fun toString(): String {
        val largestUnit = PowerUnit.International.entries.asReversed().firstOrNull { unit ->
            rawMicrowatts.absoluteValue / unit.microwattScale > 0
        }
        return toString(largestUnit ?: PowerUnit.International.Microwatt, decimals = 2)
    }

    // endregion

    // region Comparisons

    /**
     * Returns true if this area value is infinite.
     */
    public fun isInfinite(): Boolean = rawMicrowatts.isInfinite()

    /**
     * Returns true if this area value is finite.
     */
    public fun isFinite(): Boolean = rawMicrowatts.isFinite()

    /**
     * Compares this power with the [other] power. Returns zero if this power is equal
     * to the specified [other] power, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    public override fun compareTo(other: Power): Int {
        return rawMicrowatts.compareTo(other.rawMicrowatts)
    }

    // endregion
}
