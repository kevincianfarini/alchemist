package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline
import kotlin.text.Typography.nbsp
import kotlin.time.Duration
import kotlin.time.DurationUnit

@JvmInline
public value class Acceleration internal constructor(
    private val rawNanometersPerSecondSquared: SaturatingLong
) : Comparable<Acceleration> {

    // region SI Arithmetic

    /**
     * Returns the resulting [Velocity] after applying this acceleration for [duration].
     */
    public operator fun times(duration: Duration): Velocity = TODO()

    /**
     * Returns the resulting [Force] after applying this acceleration to [mass].
     */
    public operator fun times(mass: Mass): Force = TODO()

    // endregion

    // region Scalar Arithmetic

    /**
     * Returns the number that is the ratio of this and the [other] acceleration value.
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
     */
    public operator fun div(scale: Long): Acceleration = Acceleration(rawNanometersPerSecondSquared / scale)

    /**
     * Returns an acceleration whose value is the difference between this and the [other] acceleration value.
     */
    public operator fun minus(other: Acceleration): Acceleration {
        return Acceleration(rawNanometersPerSecondSquared - other.rawNanometersPerSecondSquared)
    }

    /**
     * Returns an acceleration whose value is the sum between this and the [other] acceleration value.
     */
    public operator fun plus(other: Acceleration): Acceleration {
        return Acceleration(rawNanometersPerSecondSquared + other.rawNanometersPerSecondSquared)
    }

    /**
     * Returns an acceleration whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Acceleration = times(scale.toLong())

    /**
     * Returns an acceleration whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Acceleration = Acceleration(rawNanometersPerSecondSquared * scale)

    // endregion

    // region Acceleration to Scalar Conversions

    /**
     * Returns the value of this acceleration expressed as a [Double] number of the specific [lengthUnit] per
     * [durationUnit]². Infinite values are converted to either [Double.POSITIVE_INFINITY] or [Double.NEGATIVE_INFINITY]
     * depending on its sign.
     */
    public fun toDouble(lengthUnit: LengthUnit, durationUnit: DurationUnit): Double {
        val lengthPerSecond2 = rawNanometersPerSecondSquared.toDouble() / lengthUnit.nanometerScale.toDouble()
        return lengthPerSecond2 * durationUnit.secondScale * durationUnit.secondScale
    }

    /**
     * Returns a fractional string representation of this acceleration expressed in the specified [lengthUnit] per
     * [durationUnit]².
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

    override fun compareTo(other: Acceleration): Int {
        return rawNanometersPerSecondSquared.compareTo(other.rawNanometersPerSecondSquared)
    }

    // endregion

    public companion object {
        public val POSITIVE_INFINITY: Acceleration = Acceleration(SaturatingLong.POSITIVE_INFINITY)
        public val NEGATIVE_INFINITY: Acceleration = Acceleration(SaturatingLong.NEGATIVE_INFINITY)
    }
}

internal val Int.nmPerSecond2: Acceleration get() = toLong().nmPerSecond2
internal val Long.nmPerSecond2: Acceleration get() = Acceleration(saturated)