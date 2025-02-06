package io.github.kevincianfarini.alchemist.type

import io.github.kevincianfarini.alchemist.internal.SaturatingLong
import io.github.kevincianfarini.alchemist.internal.saturated
import io.github.kevincianfarini.alchemist.internal.toDecimalString
import io.github.kevincianfarini.alchemist.scalar.mm2
import io.github.kevincianfarini.alchemist.unit.AreaUnit
import io.github.kevincianfarini.alchemist.unit.LengthUnit
import kotlin.jvm.JvmInline
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * Represents a measure of area and is capable of storing ±9.22 million kilometers² at millimeter² precision.
 */
@JvmInline
public value class Area internal constructor(internal val rawMillimetersSquared: SaturatingLong) : Comparable<Area> {

    // region SI Arithmetic

    /**
     * Returns the resulting length after dividing this area by the specified [length].
     *
     * This operation attempts to retain precision, but for sufficiently large values of this area some precision
     * may be lost.
     *
     * @throws IllegalArgumentException if both this area and [length] are infinite.
     */
    public operator fun div(length: Length): Length {
        // Try to find the right level which we can perform this operation at without losing precision.
        // --------------------------------------------------------------------------------------------
        // 1 nanometer² / 1 nanometer is 1 nanometer
        // 1 micrometer² / 1 nanometer is 1,000,000 nanometers.
        // 1 millimeter² / 1 nanometer is 1,000,000,000,000 nanometers.
        // --------------------------------------------------------------------------------------------
        val nano2 = rawMillimetersSquared * 1_000_000_000_000
        if (nano2.isFinite()) return Length(nano2 / length.rawNanometers)
        val micro2 = rawMillimetersSquared * 1_000_000
        if (micro2.isFinite()) return Length((micro2 / length.rawNanometers) * 1_000_000)
        return Length((rawMillimetersSquared / length.rawNanometers) * 1_000_000_000_000)
    }

    /**
     * Returns the resulting [Volume] after applying this area over the specified [length].
     *
     * This operation attempts to retain precision, but for sufficiently large values of this area or the
     * specified [length] some precision may be lost.
     *
     * @throws IllegalArgumentException if this area is [infinite][isInfinite] and [length] is zero, or if this area
     * is zero and [length] is infinite.
     */
    public operator fun times(length: Length): Volume {
        return length.toInternationalComponents { giga, mega, kilo, meters, centi, milli, micro, nano ->
            // Try to find the right level which we can perform this operation at without losing precision.
            // --------------------------------------------------------------------------------------------
            // 1 millimeter² * 1 nm is 1 picoliter.
            // 1 millimeter² * 1 μm is 1 nanoliter.
            // 1 millimeter² * 1 mm is 1 microliter.
            // 1 millimeter² * 1 cm is 10 microliters.
            // 1 millimeter² * 1 m is 1 milliliter.
            // 1 millimeter² * 1 km is 1 liter.
            // 1 millimeter² * 1 Mm is 1 kiloliter.
            // 1 millimeter² * 1 Gm is 1 megaliter.
            // --------------------------------------------------------------------------------------------
            val megaliters = rawMillimetersSquared * giga
            val kiloliters = rawMillimetersSquared * mega
            val liters = rawMillimetersSquared * kilo
            val milliliters = rawMillimetersSquared * meters
            val microliters = (rawMillimetersSquared * centi * 10) + (rawMillimetersSquared * milli)
            val nanoliters = rawMillimetersSquared * micro
            val picoliters = rawMillimetersSquared * nano
            // ----------- Try picoliter precision. ------------------------------------------------------
            val picoL = picoliters + (nanoliters * 1_000) + (microliters * 1_000_000) + (milliliters * 1_000_000_000) + (liters * 1_000_000_000_000) + (kiloliters * 1_000_000_000_000_000) + (megaliters * 1_000_000_000_000_000_000)
            if (picoL.isFinite()) return@toInternationalComponents Volume(picoL / 1_000_000_000)
            // ----------- Try nanoliter precision. ------------------------------------------------------
            val nanoL = (picoliters / 1_000) + nanoliters + (microliters * 1_000) + (milliliters * 1_000_000) + (liters * 1_000_000_000) + (kiloliters * 1_000_000_000_000) + (megaliters * 1_000_000_000_000_000)
            if (nanoL.isFinite()) return@toInternationalComponents Volume(nanoL / 1_000_000)
            // ----------- Try microliter precision. -----------------------------------------------------
            val microL = (picoliters / 1_000_000) + (nanoliters / 1_000) + microliters + (milliliters * 1_000) + (liters * 1_000_000) + (kiloliters * 1_000_000_000) + (megaliters * 1_000_000_000_000)
            if (microL.isFinite()) return@toInternationalComponents Volume(microL / 1_000)
            // ----------- Default milliliter precision. -------------------------------------------------
            val milliL = (picoliters / 1_000_000_000) + (nanoliters / 1_000_000) + (microliters / 1_000) + milliliters + (liters * 1_000) + (kiloliters * 1_000_000) + (megaliters * 1_000_000_000)
            Volume(milliL)
        }
    }

    // endregion

    // region Scalar Arithmetic

    /**
     * Returns an area whose value is this area value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Area = div(scale.toLong())

    /**
     * Returns an area whose value is this area value divided by the specified [scale].
     *
     * @throws IllegalArgumentException when [scale] is equal to [Long.MAX_VALUE] or [Long.MIN_VALUE] and this
     * area is [infinite][isInfinite].
     */
    public operator fun div(scale: Long): Area = Area(rawMillimetersSquared / scale)

    /**
     * Returns the number that is the ratio of this and the [other] area value.
     *
     * @throws IllegalArgumentException when both this and the [other] area are [infinite][isInfinite].
     */
    public operator fun div(other: Area): Double {
        return rawMillimetersSquared.toDouble() / other.rawMillimetersSquared.toDouble()
    }

    /**
     * Returns the negative of this area value.
     */
    public operator fun unaryMinus(): Area = Area(-rawMillimetersSquared)

    /**
     * Returns an area whose value is the difference between this and the [other] area value.
     *
     * @throws IllegalArgumentException if this area and the [other] area are both
     * [infinite][isInfinite] but have equivalent signs.
     */
    public operator fun minus(other: Area): Area = Area(rawMillimetersSquared - other.rawMillimetersSquared)

    /**
     * Returns an area whose value is the sum between this and the [other] area value.
     *
     * @throws IllegalArgumentException if this area and the [other] area are both
     * [infinite][isInfinite] but have differing signs.
     */
    public operator fun plus(other: Area): Area = Area(rawMillimetersSquared + other.rawMillimetersSquared)

    /**
     * Returns an area whose value is multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this area is [infinite][isInfinite] and [scale] is 0.
     */
    public operator fun times(scale: Int): Area = times(scale.toLong())

    /**
     * Returns an area whose value is multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this area is [infinite][isInfinite] and [scale] is 0, or when this
     * area is 0 and scale is [Long.MAX_VALUE] or [Long.MIN_VALUE].
     */
    public operator fun times(scale: Long): Area = Area(rawMillimetersSquared * scale)

    /**
     * Returns an area whose value is multiplied by the specified [scale]. This operation may be rounded when the result
     * cannot be precisely represented with a [Double] number.
     *
     * @throws IllegalArgumentException when this area is [infinite][isInfinite] and [scale] is 0.0 or when this area is 0
     * and scale is [infinite][Double.isInfinite].
     */
    public operator fun times(scale: Double): Area = Area(rawMillimetersSquared * scale)

    /**
     * Returns an area whose value is divided by the specified [scale]. This operation may be rounded when the result
     * cannot be precisely represented with a [Double] number.
     *
     * @throws IllegalArgumentException when this area is [infinite][isInfinite] and [scale] is 0.0 or when this area is 0
     * and scale is [infinite][Double.isInfinite].
     */
    public operator fun div(scale: Double): Area = Area(rawMillimetersSquared / scale)

    // endregion

    // region Area to Scalar Conversions

    /**
     * Splits this area into megameters², kilometers², meters², centimeters², and millimeters² and executes the [action]
     * with those components. The result of [action] is returned as the result of this function.
     *
     * Infinite areas invoke [action] with [Long.MAX_VALUE] or [Long.MIN_VALUE] for every component, depending on the
     * infinite value's sign.
     */
    public fun <T> toInternationalComponents(
        action: (
            megametersSquared: Long,
            kilometersSquared: Long,
            metersSquared: Long,
            centimetersSquared: Long,
            millimetersSquared: Long,
        ) -> T,
    ): T {
        val mega = rawMillimetersSquared / 1_000_000_000_000_000_000
        val megaRemainder = rawMillimetersSquared % 1_000_000_000_000_000_000
        val kilo = megaRemainder / 1_000_000_000_000
        val kiloRemainder = megaRemainder % 1_000_000_000_000
        val meters = kiloRemainder / 1_000_000
        val metersRemainder = kiloRemainder % 1_000_000
        val centi = metersRemainder / 100
        val milli = metersRemainder % 100
        return action(mega.rawValue, kilo.rawValue, meters.rawValue, centi.rawValue, milli.rawValue)
    }

    /**
     * Returns the value of this area expressed as a [Long] number of the specified [unit]. Infinite values are
     * converted to either [Long.MAX_VALUE] or [Long.MIN_VALUE] depending on its sign.
     */
    public fun toLong(unit: AreaUnit): Long {
        return (rawMillimetersSquared / unit.millimetersSquaredScale).rawValue
    }

    /**
     * Returns the value of this area expressed as a [Long] number of the specified [squareUnit]². Infinite
     * values are converted to either [Long.MAX_VALUE] or [Long.MIN_VALUE] depending on its sign.
     */
    public fun toLong(squareUnit: LengthUnit): Long {
        val nm2 = rawMillimetersSquared * 1_000_000_000_000
        val nm2Scale = squareUnit.nanometerScale.saturated * squareUnit.nanometerScale
        return if (nm2.isFinite() && nm2Scale.isFinite()) {
            nm2 / nm2Scale
        } else {
            val um2 = rawMillimetersSquared * 1_000_000
            val um2Unit = squareUnit.nanometerScale.saturated / 1_000
            val um2Scale = um2Unit * um2Unit
            if (um2.isFinite() && um2Scale.isFinite()) {
                um2 / um2Scale
            } else {
                val mm2Unit = squareUnit.nanometerScale.saturated / 1_000_000
                val mm2Scale = mm2Unit * mm2Unit
                if (rawMillimetersSquared.isFinite() && mm2Scale.isFinite()) {
                    rawMillimetersSquared / mm2Scale
                } else {
                    0L.saturated
                }
            }
        }.rawValue
    }

    /**
     * Returns the value of this area expressed as a [Double] number of the specified [unit]. Infinite values are
     * converted to either [Double.POSITIVE_INFINITY] or [Double.NEGATIVE_INFINITY] depending on its sign.
     */
    public fun toDouble(unit: AreaUnit): Double {
        return this / unit.millimetersSquaredScale.mm2
    }

    /**
     * Returns the value of this area expressed as a [Double] number of the specific [LengthUnit]². Infinite values are
     * converted to either [Double.POSITIVE_INFINITY] or [Double.NEGATIVE_INFINITY] depending on its sign.
     */
    public fun toDouble(squareUnit: LengthUnit): Double {
        val nm2 = rawMillimetersSquared.toDouble() * 1_000_000_000_000
        return nm2 / squareUnit.nanometerScale.toDouble().pow(2)
    }

    /**
     * Returns a fractional string representation of this area expressed in the specified [AreaUnit] and is rounded
     * to the specified [decimals].
     */
    public fun toString(unit: AreaUnit, decimals: Int = 0): String = when (isInfinite()) {
        true -> rawMillimetersSquared.toString()
        false -> buildString {
            append(toDouble(unit).toDecimalString(decimals))
            append(unit.symbol)
        }
    }

    /**
     * Returns a fractional string representation of this area expressed in the specified [LengthUnit]² and is rounded
     * to the specified [decimals].
     */
    public fun toString(squareUnit: LengthUnit, decimals: Int = 0): String = when (isInfinite()) {
        true -> rawMillimetersSquared.toString()
        false -> buildString {
            append(toDouble(squareUnit).toDecimalString(decimals))
            append(squareUnit.symbol)
            append("²")
        }
    }

    /**
     * Returns a fractional string representation of this area expressed in the largest [LengthUnit]² quantity which is
     * greater than or equal to 1.
     */
    public override fun toString(): String {
        val largestUnit = LengthUnit.International.entries.asReversed().firstOrNull { squareUnit ->
            toDouble(squareUnit) >= 1.0
        }
        return toString(largestUnit ?: LengthUnit.International.Millimeter, decimals = 2)
    }

    // endregion

    // region Comparisons

    /**
     * Returns true if this area value is infinite.
     */
    public fun isInfinite(): Boolean = rawMillimetersSquared.isInfinite()

    /**
     * Returns true if this area value is finite.
     */
    public fun isFinite(): Boolean = rawMillimetersSquared.isFinite()

    /**
     * Compares this area with the [other] area. Returns zero if this area is equal
     * to the specified [other] area, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    public override fun compareTo(other: Area): Int {
        return rawMillimetersSquared.compareTo(other.rawMillimetersSquared)
    }

    // endregion
}
