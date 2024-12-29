package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline

/**
 * Represents a measure of area and is capable of storing ±9.22 million kilometers² at millimeter² precision.
 */
@JvmInline
public value class Area internal constructor(private val rawMillimetersSquared: SaturatingLong) : Comparable<Area> {

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
     */
    public operator fun times(length: Length): Volume = TODO()

    // endregion

    // region Scalar Arithmetic

    /**
     * Returns an area whose value is this area value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Area = div(scale.toLong())

    /**
     * Returns an area whose value is this area value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Area = Area(rawMillimetersSquared / scale)

    /**
     * Returns the number that is the ratio of this and the [other] area value.
     */
    public operator fun div(other: Area): Double {
        return rawMillimetersSquared.toDouble() / other.rawMillimetersSquared.toDouble()
    }

    /**
     * Returns an area whose value is the difference between this and the [other] area value.
     */
    public operator fun minus(other: Area): Area = Area(rawMillimetersSquared - other.rawMillimetersSquared)

    /**
     * Returns an area whose value is the sum between this and the [other] area value.
     */
    public operator fun plus(other: Area): Area = Area(rawMillimetersSquared + other.rawMillimetersSquared)

    /**
     * Returns an area whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Area = times(scale.toLong())

    /**
     * Returns an area whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Area = Area(rawMillimetersSquared * scale)

    // endregion

    // region Area to Scalar Conversions

    /**
     * Returns the value of this area expressed as a [Double] number of the specified [unit]. Infinite values are
     * converted to either [Double.POSITIVE_INFINITY] or [Double.NEGATIVE_INFINITY] depending on its sign.
     */
    public fun toDouble(unit: AreaUnit): Double {
        return this / unit.millimetersSquaredScale.mm2
    }

    public fun <T> toInternationalComponents(
        action: (
            megametersSquared: Long,
            kilometersSquared: Long,
            metersSquared: Long,
            centimetersSquared: Long,
            millimetersSquared: Long,
        ) -> T,
    ): T {
        val mega = rawMillimetersSquared / AreaUnit.International.MegameterSquared.millimetersSquaredScale
        val megaRemainder = rawMillimetersSquared % AreaUnit.International.MegameterSquared.millimetersSquaredScale
        val kilo = megaRemainder / AreaUnit.International.KilometerSquared.millimetersSquaredScale
        val kiloRemainder = megaRemainder % AreaUnit.International.KilometerSquared.millimetersSquaredScale
        val meters = kiloRemainder / AreaUnit.International.MeterSquared.millimetersSquaredScale
        val metersRemainder = kiloRemainder % AreaUnit.International.MeterSquared.millimetersSquaredScale
        val centi = metersRemainder / AreaUnit.International.CentimeterSquared.millimetersSquaredScale
        val milli = metersRemainder % AreaUnit.International.CentimeterSquared.millimetersSquaredScale
        return action(mega.rawValue, kilo.rawValue, meters.rawValue, centi.rawValue, milli.rawValue)
    }

    public fun toString(unit: AreaUnit, decimals: Int = 0): String = when (rawMillimetersSquared.isInfinite()) {
        true -> rawMillimetersSquared.toString()
        false -> buildString {
            append(toDouble(unit).toDecimalString(decimals))
            append(unit.symbol)
        }
    }

    public override fun toString(): String {
        val largestUnit = AreaUnit.International.entries.asReversed().firstOrNull { unit ->
            rawMillimetersSquared.absoluteValue / unit.millimetersSquaredScale > 0
        }
        return toString(largestUnit ?: AreaUnit.International.MillimeterSquared, decimals = 2)
    }

    // endregion

    // region Comparisons

    public fun isInfinite(): Boolean = this == POSITIVE_INFINITY || this == NEGATIVE_INFINITY

    public fun isFinite(): Boolean = !isInfinite()

    public override fun compareTo(other: Area): Int {
        return rawMillimetersSquared.compareTo(other.rawMillimetersSquared)
    }

    // endregion

    public companion object {
        public val POSITIVE_INFINITY: Area = Area(SaturatingLong.POSITIVE_INFINITY)
        public val NEGATIVE_INFINITY: Area = Area(SaturatingLong.NEGATIVE_INFINITY)
    }
}

public interface AreaUnit {

    /**
     * The amount of millimeters² in this unit. Implementations of [AreaUnit] should be perfectly divisible by a
     * quantity of millimeters².
     */
    public val millimetersSquaredScale: Long

    /**
     * The symbol of this unit.
     */
    public val symbol: String

    public enum class International(
        override val millimetersSquaredScale: Long,
        override val symbol: String,
    ) : AreaUnit {
        MillimeterSquared(1, "mm²"),
        CentimeterSquared(100, "cm²"),
        MeterSquared(1_000_000, "m²"),
        KilometerSquared(1_000_000_000_000, "km²"),
        MegameterSquared(1_000_000_000_000_000_000, "Mm²"),
    }
}


internal val Long.mm2: Area get() = Area(saturated)
internal val Int.mm2: Area get() = Area(toLong().saturated)
