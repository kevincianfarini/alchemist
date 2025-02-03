package io.github.kevincianfarini.alchemist.type

import io.github.kevincianfarini.alchemist.internal.SaturatingLong
import io.github.kevincianfarini.alchemist.internal.toDecimalString
import io.github.kevincianfarini.alchemist.scalar.centimeters
import io.github.kevincianfarini.alchemist.scalar.mm2
import io.github.kevincianfarini.alchemist.unit.LengthUnit
import io.github.kevincianfarini.alchemist.unit.VolumeUnit
import kotlin.jvm.JvmInline
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * Represents a measure of volume and is capable of storing ±9.2 trillion meters³ (±9.2 quadrillion liters) at
 * centimeter³ (milliliter) precision.
 */
@JvmInline
public value class Volume internal constructor(private val rawCubicCentimeters: SaturatingLong) : Comparable<Volume> {

    // region SI Arithmetic

    /**
     * Returns the resulting [Length] after dividing this volume over the specified [area].
     *
     * This operation attempts to retain precision, but for sufficiently large values of either this volume or
     * [area], some precision may be lost.
     *
     * @throws IllegalArgumentException if both this volume and [area] are infinite.
     */
    public operator fun div(area: Area): Length {
        // TODO This is simplistic and we should attempt to retain precision in the future.
        return ((rawCubicCentimeters / area.rawMillimetersSquared) * 100).centimeters
    }

    /**
     * Returns the resulting [Area] after dividing this volume over the specified [length].
     *
     * This operation attempts to retain precision, but for sufficiently large values of either this volume or
     * [length], some precision may be lost.
     *
     * @throws IllegalArgumentException if both this volume and [length] are infinite.
     */
    public operator fun div(length: Length): Area {
        // TODO This is simplistic and we should attempt to retain precision in the future.
        return ((rawCubicCentimeters / length.rawNanometers) * 1_000_000_000).mm2
    }

    // endregion

    // region Scalar Arithmetic

    /**
     * Returns the number that is the ratio of this and the [other] volume value.
     *
     * @throws IllegalArgumentException when both this and the [other] volume are [infinite][isInfinite].
     */
    public operator fun div(other: Volume): Double {
        return rawCubicCentimeters.toDouble() / other.rawCubicCentimeters.toDouble()
    }

    /**
     * Returns a volume whose value is this volume value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Volume = div(scale.toLong())

    /**
     * Returns a volume whose value is this volume value divided by the specified [scale].
     *
     * @throws IllegalArgumentException when [scale] is equal to [Long.MAX_VALUE] or [Long.MIN_VALUE] and this
     * volume is [infinite][isInfinite].
     */
    public operator fun div(scale: Long): Volume = Volume(rawCubicCentimeters / scale)

    /**
     * Returns the negative of this volume value.
     */
    public operator fun unaryMinus(): Volume = Volume(-rawCubicCentimeters)

    /**
     * Returns a volume whose value is the difference between this and the [other] volume value.
     *
     * @throws IllegalArgumentException if this volume and the [other] volume are both
     * [infinite][isInfinite] but have equivalent signs.
     */
    public operator fun minus(other: Volume): Volume = Volume(rawCubicCentimeters - other.rawCubicCentimeters)

    /**
     * Returns a volume whose value is the sum between this and the [other] volume value.
     *
     * @throws IllegalArgumentException if this volume and the [other] volume are both
     * [infinite][isInfinite] but have differing signs.
     */
    public operator fun plus(other: Volume): Volume = Volume(rawCubicCentimeters + other.rawCubicCentimeters)

    /**
     * Returns a volume whose value is multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this volume is [infinite][isInfinite] and [scale] is 0.
     */
    public operator fun times(scale: Int): Volume = times(scale.toLong())

    /**
     * Returns a volume whose value is multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this volume is [infinite][isInfinite] and [scale] is 0, or when this
     * volume is 0 and scale is [Long.MAX_VALUE] or [Long.MIN_VALUE].
     */
    public operator fun times(scale: Long): Volume = Volume(rawCubicCentimeters * scale)

    /**
     * Returns a volume whose value is multiplied by the specified [scale]. This operation may be rounded when the result
     * cannot be precisely represented with a [Double] number.
     *
     * @throws IllegalArgumentException when this volume is [infinite][isInfinite] and [scale] is 0.0 or when this volume is 0
     * and scale is [infinite][Double.isInfinite].
     */
    public operator fun times(scale: Double): Volume = Volume(rawCubicCentimeters * scale)

    /**
     * Returns a volume whose value is divided by the specified [scale]. This operation may be rounded when the result
     * cannot be precisely represented with a [Double] number.
     *
     * @throws IllegalArgumentException when this volume is [infinite][isInfinite] and [scale] is 0.0 or when this volume is 0
     * and scale is [infinite][Double.isInfinite].
     */
    public operator fun div(scale: Double): Volume = Volume(rawCubicCentimeters / scale)

    // endregion

    // region Volume to Scalar Conversions

    /**
     * Returns the value of this velocity expressed as a [Long] number of the specified [unit]. Infinite values
     * are converted to either [Long.MAX_VALUE] or [Long.MIN_VALUE] depending on its sign.
     */
    public fun toLong(unit: VolumeUnit): Long {
        return (rawCubicCentimeters / unit.cubicCentimetersScale).rawValue
    }

    /**
     * Returns the value of this velocity expressed as a [Long] number of the specified [LengthUnit]³. Infinite values
     * are converted to either [Long.MAX_VALUE] or [Long.MIN_VALUE] depending on its sign.
     */
    public fun toLong(cubicUnit: LengthUnit): Long {
        return toDouble(cubicUnit).roundToLong()
    }

    /**
     * Returns the value of this volume expressed as a [Double] number of the specified [unit]. Infinite values
     * are converted to either [Double.POSITIVE_INFINITY] or [Double.NEGATIVE_INFINITY] dependeing on its sign.
     */
    public fun toDouble(unit: VolumeUnit): Double {
        return rawCubicCentimeters.toDouble() / unit.cubicCentimetersScale.toDouble()
    }

    /**
     * Returns the value of this volume expressed as a [Double] number of the specified [LengthUnit]³. Infinite values
     * are converted to either [Double.POSITIVE_INFINITY] or [Double.NEGATIVE_INFINITY] dependeing on its sign.
     */
    public fun toDouble(cubicUnit: LengthUnit): Double {
        val cubicNanos = rawCubicCentimeters.toDouble() * 1e21
        return cubicNanos / cubicUnit.nanometerScale.toDouble().pow(3)
    }

    /**
     * Returns a fractional string representation of this volume expressed in the specified [unit] and is rounded
     * to the specified [decimals].
     */
    public fun toString(unit: VolumeUnit, decimals: Int = 0): String = when {
        isInfinite() -> rawCubicCentimeters.toString()
        else -> buildString {
            append(toDouble(unit).toDecimalString(decimals))
            append(unit.symbol)
        }
    }

    /**
     * Returns a fractional string representation of this volume expressed in the specified [LengthUnit]³
     * and is rounded to the specified [decimals].
     */
    public fun toString(cubicUnit: LengthUnit, decimals: Int = 0): String = when {
        isInfinite() -> rawCubicCentimeters.toString()
        else -> buildString {
            append(toDouble(cubicUnit).toDecimalString(decimals))
            append(cubicUnit.symbol)
            append("³")
        }
    }

    /**
     * Returns a fractional string representation of this volume expressed in the largest [LengthUnit]³ quantity
     * which is greater than or equal to 1.
     */
    public override fun toString(): String {
        val lengthUnit = LengthUnit.International.entries.asReversed().firstOrNull { cubicUnit ->
            toDouble(cubicUnit) >= 1.0
        }
        return toString(lengthUnit ?: LengthUnit.International.Centimeter, decimals = 2)
    }

    // endregion

    // region Comparisons

    /**
     * Returns true if this volume value is infinite.
     */
    public fun isInfinite(): Boolean = rawCubicCentimeters.isInfinite()

    /**
     * Returns true if this volume value is finite.
     */
    public fun isFinite(): Boolean = rawCubicCentimeters.isFinite()

    /**
     * Compares this volume with the [other] volume. Returns zero if this volume is equal
     * to the specified [other] volume, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    override fun compareTo(other: Volume): Int = rawCubicCentimeters.compareTo(other.rawCubicCentimeters)

    // endregion
}
