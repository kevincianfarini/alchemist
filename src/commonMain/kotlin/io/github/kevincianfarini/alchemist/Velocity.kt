package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline
import kotlin.text.Typography.nbsp
import kotlin.time.Duration
import kotlin.time.DurationUnit

@JvmInline
public value class Velocity internal constructor(
    internal val rawNanometersPerSecond: SaturatingLong
) : Comparable<Velocity> {

    // region SI Arithmetic

    /**
     * Returns the [Acceleration] necessary to achieve this velocity when applied over the specified [duration].
     */
    public operator fun div(duration: Duration): Acceleration = TODO()

    /**
     * Returns the [Length] traveled at this velocity for the specified [duration].
     */
    public operator fun times(duration: Duration): Length = duration.toComponents { seconds, nanoseconds ->
        val secondComponent = (rawNanometersPerSecond * seconds).nanometers
        val preciseNanosecondComponent = ((rawNanometersPerSecond * nanoseconds) / 1_000_000_000).nanometers
        if (preciseNanosecondComponent.isFinite()) {
            secondComponent + preciseNanosecondComponent
        } else {
            val coarseNanosecondComponent = ((rawNanometersPerSecond / 1_000_000_000) * nanoseconds).nanometers
            secondComponent + coarseNanosecondComponent
        }
    }

    // endregion

    // region Scalar Arithmetic

    /**
     * Returns the number that is the ratio of this and the [other] velocity value.
     */
    public operator fun div(other: Velocity): Double {
        return rawNanometersPerSecond.toDouble() / other.rawNanometersPerSecond.toDouble()
    }

    /**
     * Returns a velocity whose value is this velocity value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Velocity = div(scale.toLong())

    /**
     * Returns a velocity whose value is this velocity value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Velocity = Velocity(rawNanometersPerSecond / scale)

    /**
     * Returns a velocity whose value is the difference between this and the [other] velocity value.
     */
    public operator fun minus(other: Velocity): Velocity {
        return Velocity(rawNanometersPerSecond - other.rawNanometersPerSecond)
    }

    /**
     * Returns a velocity whose value is the sum between this and the [other] velocity value.
     */
    public operator fun plus(other: Velocity): Velocity {
        return Velocity(rawNanometersPerSecond + other.rawNanometersPerSecond)
    }

    /**
     * Returns a velocity whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Velocity = times(scale.toLong())

    /**
     * Returns a velocity whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Velocity = Velocity(rawNanometersPerSecond * scale)

    // endregion

    // region Velocity to Scalar Conversions

    /**
     * Returns the value of this velocity expressed as a [Double] number of the specific [lengthUnit] per
     * [durationUnit]. Infinite values are converted to either [Double.POSITIVE_INFINITY] or [Double.NEGATIVE_INFINITY]
     * depending on its sign.
     */
    public fun toDouble(lengthUnit: LengthUnit, durationUnit: DurationUnit = DurationUnit.SECONDS): Double {
        return (rawNanometersPerSecond.toDouble() / lengthUnit.nanometerScale.toDouble()) * durationUnit.secondScale
    }

    /**
     * Returns a fractional string representation of this velocity expressed in the specified [lengthUnit] per
     * [durationUnit].
     */
    public fun toString(
        lengthUnit: LengthUnit,
        durationUnit: DurationUnit = DurationUnit.SECONDS,
        decimals: Int = 0,
    ): String {
        return when (isInfinite()) {
            true -> rawNanometersPerSecond.toString()
            false -> buildString {
                append(toDouble(lengthUnit, durationUnit).toDecimalString(decimals))
                append(nbsp)
                append(lengthUnit.symbol)
                append("/")
                append(durationUnit.shortName)
            }
        }
    }

    override fun toString(): String {
        val lengthUnit = LengthUnit.International.entries.asReversed().firstOrNull { unit ->
            rawNanometersPerSecond.absoluteValue / unit.nanometerScale > 0
        }
        return toString(lengthUnit ?: LengthUnit.International.Nanometer, DurationUnit.SECONDS, decimals = 2)
    }

    // endregion

    // region Comparisons

    public fun isFinite(): Boolean = rawNanometersPerSecond.isFinite()

    public fun isInfinite(): Boolean = rawNanometersPerSecond.isInfinite()

    override fun compareTo(other: Velocity): Int = rawNanometersPerSecond.compareTo(other.rawNanometersPerSecond)

    // endregion

    public companion object {
        public val POSITIVE_INFINITY: Velocity = Velocity(SaturatingLong.POSITIVE_INFINITY)
        public val NEGATIVE_INFINITY: Velocity = Velocity(SaturatingLong.NEGATIVE_INFINITY)
    }
}

internal val Int.nmPerSecond: Velocity get() = Velocity(toLong().saturated)
internal val Long.nmPerSecond: Velocity get() = Velocity(saturated)
internal val SaturatingLong.nmPerSecond: Velocity get() = Velocity(this)
