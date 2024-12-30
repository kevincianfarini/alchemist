package io.github.kevincianfarini.alchemist

import io.github.kevincianfarini.alchemist.internal.POSITIVE_INFINITY
import io.github.kevincianfarini.alchemist.internal.SaturatingLong
import io.github.kevincianfarini.alchemist.internal.isPreciseToNanosecond
import io.github.kevincianfarini.alchemist.internal.saturated
import io.github.kevincianfarini.alchemist.internal.sign
import io.github.kevincianfarini.alchemist.internal.toDecimalString
import kotlin.jvm.JvmInline
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Represents a measure of length and is capable of storing ±9.2 million kilometers at nanometer precision.
 */
@JvmInline
public value class Length internal constructor(internal val rawNanometers: SaturatingLong) : Comparable<Length> {

    // region SI Arithmetic

    /**
     * Returns the constant [Velocity] required to travel this length in the specified [duration].
     *
     * This operation attempts to retain precision, but for sufficiently large values of either this length or the
     * other [duration], some precision may be lost.
     *
     * @throws IllegalArgumentException if both this length and [duration] are infinite.
     */
    public operator fun div(duration: Duration): Velocity = when {
        rawNanometers.isInfinite() && duration.isInfinite() ->  {
            throw IllegalArgumentException("Dividing two infinite values yields an undefined result.")
        }
        rawNanometers.isInfinite() -> Velocity(rawNanometers * duration.sign)
        duration.isInfinite() -> Velocity(0L.saturated)
        else -> calculateVelocity(duration)
    }

    private fun calculateVelocity(duration: Duration): Velocity {
        // Try to find the right level which we can perform this operation at without losing precision.
        if (duration.isPreciseToNanosecond()) {
            val velocity = nanosPerNs(rawNanometers, duration.inWholeNanoseconds)
            if (velocity.isFinite()) return velocity
        }
        val ms = duration.inWholeMilliseconds
        val velcity = nanosPerMs(rawNanometers, ms)
        if (velcity.isFinite()) return velcity
        return Velocity((rawNanometers / ms) * 1_000)
    }

    private fun nanosPerMs(nanos: SaturatingLong, ms: Long): Velocity {
        // 1 nanometer per 1 millisecond is 1,000 nanometers / second.
        val nanosPerMs = nanos / ms
        val picoRemainder = (nanos % ms) * 1_000
        return (nanosPerMs * 1_000).nmPerSecond + picosPerMs(picoRemainder, ms)
    }

    private fun picosPerMs(picos: SaturatingLong, ms: Long): Velocity {
        // 1 picometer per 1 millisecond is 1 nanometer / second.
        return Velocity(picos / ms)
    }

    private fun nanosPerNs(nanos: SaturatingLong, ns: Long): Velocity {
        // 1 nanometer per 1 nanosecond is 1,000,000,000 nanometers / second.
        val nanosPerNs = nanos / ns
        val picoRemainder = (nanos % ns) * 1_000
        return (nanosPerNs * 1_000_000_000).nmPerSecond + picosPerNs(picoRemainder, ns)
    }

    private fun picosPerNs(picos: SaturatingLong, ns: Long): Velocity {
        // 1 picometer per 1 nanosecond is 1,000,000 nanometers / second.
        val picosPerNs = picos / ns
        val femtoRemainder = (picos % ns) * 1_000
        return (picosPerNs * 1_000_000).nmPerSecond + femtosPerNs(femtoRemainder, ns)
    }

    private fun femtosPerNs(femtos: SaturatingLong, ns: Long): Velocity {
        // 1 femtometer per 1 nanosecond is 1,000 nanometers / second.
        val femtosPerNs = femtos / ns
        val attoRemainder = (femtos % ns) * 1_000
        return (femtosPerNs * 1_000).nmPerSecond + attometersPerNs(attoRemainder, ns)
    }

    private fun attometersPerNs(attos: SaturatingLong, ns: Long): Velocity {
        // 1 attometer per 1 nanosecond is 1 nanometer / second.
        return Velocity(attos / ns)
    }

    /**
     * Returns the [Duration] required to travel this length at the specified constant [velocity].
     *
     * This operation attempts to retain precision, but for sufficiently large values of either this length or the
     * other [velocity], some precision may be lost.
     *
     * @throws IllegalArgumentException if both this length and [velocity] are infinite.
     */
    public operator fun div(velocity: Velocity): Duration = when {
        isInfinite() && velocity.isInfinite() -> {
            throw IllegalArgumentException("Dividing two infinite values yields an undefined result.")
        }
        isInfinite() -> Duration.INFINITE / velocity.rawNanometersPerSecond.sign / rawNanometers.sign
        velocity.isInfinite() -> Duration.ZERO
        else -> calculateDuration(velocity)
    }

    private fun calculateDuration(velocity: Velocity): Duration {
        val duration = seconds(rawNanometers, velocity.rawNanometersPerSecond)
        return if (duration.isFinite()) {
            duration
        } else {
            // Do coarse operation to avoid returning infinity.
            (rawNanometers / velocity.rawNanometersPerSecond).rawValue.seconds
        }
    }

    private fun seconds(nanometers: SaturatingLong, nanometersPerSecond: SaturatingLong): Duration {
        // 1 nanometer divided by 1 nm/s is 1 second.
        val seconds = (nanometers / nanometersPerSecond).rawValue.seconds
        val picometers = (nanometers % nanometersPerSecond) * 1_000
        return seconds + milliseconds(picometers, nanometersPerSecond)
    }

    private fun milliseconds(picometers: SaturatingLong, nanometersPerSecond: SaturatingLong): Duration {
        // 1 picometer divided by 1 nm/s is 1 millisecond.
        val milliseconds = (picometers / nanometersPerSecond).rawValue.milliseconds
        val femtometers = (picometers % nanometersPerSecond) * 1_000
        return milliseconds + microseconds(femtometers, nanometersPerSecond)
    }

    private fun microseconds(femtometers: SaturatingLong, nanometersPerSecond: SaturatingLong): Duration {
        // 1 femtometer divided by 1 nm/s is 1 microsecond.
        val microseconds = (femtometers / nanometersPerSecond).rawValue.microseconds
        val attometers = (femtometers % nanometersPerSecond) * 1_000
        return microseconds + nanoseconds(attometers, nanometersPerSecond)
    }

    private fun nanoseconds(attometers: SaturatingLong, nanometersPerSecond: SaturatingLong): Duration {
        // 1 attometer divided by 1 nm/s is 1 nanosecond.
        return (attometers / nanometersPerSecond).rawValue.nanoseconds
    }

    /**
     * Returns the resulting [Area] after multiplying this length by the [other] length value.
     *
     * This operation attempts to retain precision, but for sufficiently large values of either this length or the
     * [other] length, some precision may be lost.
     *
     * @throws IllegalArgumentException when this energy is [infinite][isInfinite] and [other] is 0 or vice versa.
     */
    public operator fun times(other: Length): Area {
        // Omit micrometer and nanometer components for now. The maximum value these components could ever produce is
        // 998,001,998,001 nanometers², and therefore micrometers and nanometers are always lost to precision rounding
        // when converting to millimeters². In the future we may choose more precise measures of Area and this might
        // be revisited.
        return toSaturatedInternationalComponents { giga, mega, kilo, meters, centi, milli, _, _ ->
            other.toSaturatedInternationalComponents { otherGiga, otherMega, otherKilo, otherMeters, otherCenti, otherMilli, _, _ ->
                val gigaSquared = giga * otherGiga
                val megaSquared = mega * otherMega
                val kiloSquared = kilo * otherKilo
                val metersSquared = meters * otherMeters
                val centiSquared = centi * otherCenti
                val millisSquared = milli * otherMilli
                if (gigaSquared != 0L.saturated) {
                    // We can't represent gigameter² at millimeter² precision.
                    Area(POSITIVE_INFINITY * gigaSquared.sign)
                } else {
                    val megaMillis = megaSquared * 1_000_000_000_000_000_000
                    val kiloMillis = kiloSquared * 1_000_000_000_000
                    val meterMillis = metersSquared * 1_000_000
                    val centiMillis = centiSquared * 100
                    Area(megaMillis + kiloMillis + meterMillis + centiMillis + millisSquared)
                }
            }
        }
    }

    /**
     * Returns an [Area] representing a square with two dimensions of this length.
     *
     * This operation attempts to retain precision, but for sufficiently large values of either this length some
     * precision may be lost.
     */
    public fun squared(): Area = this * this

    /**
     * Returns a [Volume] representing a cube with three dimensions of this length.
     *
     * This operation attempts to retain precision, but for sufficiently large values of either this length some
     * precision may be lost.
     */
    public fun cubed(): Volume = this * this * this

    // endregion

    // region Scalar Arithmetic

    /**
     * Returns the number that is the ratio of this and the [other] length value.
     *
     * @throws IllegalArgumentException when both this and the [other] length are [infinite][isInfinite].
     */
    public operator fun div(other: Length): Double {
        return rawNanometers.toDouble() / other.rawNanometers.toDouble()
    }

    /**
     * Returns a length whose value is this length value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Length {
        return div(scale.toLong())
    }

    /**
     * Returns a length whose value is this length value divided by the specified [scale].
     *
     * @throws IllegalArgumentException when [scale] is equal to [Long.MAX_VALUE] or [Long.MIN_VALUE] and this
     * length is [infinite][isInfinite].
     */
    public operator fun div(scale: Long): Length {
        return Length(rawNanometers / scale)
    }

    /**
     * Returns a length whose value is the difference between this and the [other] length value.
     *
     * @throws IllegalArgumentException if this length and the [other] length are both
     * [infinite][isInfinite] but have equivalent signs.
     */
    public operator fun minus(other: Length): Length {
        return Length(rawNanometers - other.rawNanometers)
    }

    /**
     * Returns a length whose value is the sum between this and the [other] length value.
     *
     * @throws IllegalArgumentException if this length and the [other] length are both
     * [infinite][isInfinite] but have differing signs.
     */
    public operator fun plus(other: Length): Length {
        return Length(rawNanometers + other.rawNanometers)
    }

    /**
     * Returns a length whose value is multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this length is [infinite][isInfinite] and [scale] is 0.
     */
    public operator fun times(scale: Int): Length {
        return times(scale.toLong())
    }

    /**
     * Returns a length whose value is multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this length is [infinite][isInfinite] and [scale] is 0, or when this
     * length is 0 and scale is [Long.MAX_VALUE] or [Long.MIN_VALUE].
     */
    public operator fun times(scale: Long): Length {
        return Length(rawNanometers * scale)
    }

    // endregion

    // region Length to Scalar Conversions

    private fun <T> toSaturatedInternationalComponents(
        action: (
            gigameters: SaturatingLong,
            megameters: SaturatingLong,
            kilometers: SaturatingLong,
            meters: SaturatingLong,
            centimeters: SaturatingLong,
            millimeters: SaturatingLong,
            micrometers: SaturatingLong,
            nanometers: SaturatingLong,
        ) -> T,
    ): T {
        val giga = rawNanometers / LengthUnit.International.Gigameter.nanometerScale
        val gigaRemainder = rawNanometers % LengthUnit.International.Gigameter.nanometerScale
        val mega = gigaRemainder / LengthUnit.International.Megameter.nanometerScale
        val megaRemainder = gigaRemainder % LengthUnit.International.Megameter.nanometerScale
        val kilo = megaRemainder / LengthUnit.International.Kilometer.nanometerScale
        val kiloRemainder = megaRemainder % LengthUnit.International.Kilometer.nanometerScale
        val meters = kiloRemainder / LengthUnit.International.Meter.nanometerScale
        val metersRemainder = kiloRemainder % LengthUnit.International.Meter.nanometerScale
        val centi = metersRemainder / LengthUnit.International.Centimeter.nanometerScale
        val centiRemainder = metersRemainder % LengthUnit.International.Centimeter.nanometerScale
        val milli = centiRemainder / LengthUnit.International.Millimeter.nanometerScale
        val milliRemainder = centiRemainder % LengthUnit.International.Millimeter.nanometerScale
        val micro = milliRemainder / LengthUnit.International.Micrometer.nanometerScale
        val nano = milliRemainder % LengthUnit.International.Micrometer.nanometerScale
        return action(giga, mega, kilo, meters, centi, milli, micro, nano)
    }

    /**
     * Splits this length into gigameters, megameters, kilometers, meters, centimeters, millimeters, micrometers, and
     * nanometers and executes the [action] with those components. The result of [action] is returned as the result of
     * this function.
     *
     * Infinite length values invoke [action] with [Long.MAX_VALUE] or [Long.MIN_VALUE] for every component, depending
     * on the infinite value's sign.
     */
    public fun <T> toInternationalComponents(
        action: (
            gigameters: Long,
            megameters: Long,
            kilometers: Long,
            meters: Long,
            centimeters: Long,
            millimeters: Long,
            micrometers: Long,
            nanometers: Long,
        ) -> T,
    ): T = toSaturatedInternationalComponents { giga, mega, kilo, meters, centi, milli, micro, nano ->
        action(
            giga.rawValue,
            mega.rawValue,
            kilo.rawValue,
            meters.rawValue,
            centi.rawValue,
            milli.rawValue,
            micro.rawValue,
            nano.rawValue,
        )
    }

    /**
     * Splits this length into miles, yards, feet, and inches and executes the [action] with those components. The
     * result of [action] is returned as the result of this function.
     *
     * Infinite length values invoke [action] with [Long.MAX_VALUE], [Long.MIN_VALUE], [Double.POSITIVE_INFINITY], or
     * [Double.NEGATIVE_INFINITY] for every component, depending on the infinite value's sign and the component's type.
     */
    public fun <T> toUnitedStatesCustomaryComponents(
        action: (miles: Long, yards: Long, feet: Long, inches: Double) -> T,
    ): T {
        val miles = rawNanometers / LengthUnit.UnitedStatesCustomary.Mile.nanometerScale
        val milesRemainder = rawNanometers % LengthUnit.UnitedStatesCustomary.Mile.nanometerScale
        val yards = milesRemainder / LengthUnit.UnitedStatesCustomary.Yard.nanometerScale
        val yardRemainder = milesRemainder % LengthUnit.UnitedStatesCustomary.Yard.nanometerScale
        val feet = yardRemainder / LengthUnit.UnitedStatesCustomary.Foot.nanometerScale
        val feetRemainder = yardRemainder % LengthUnit.UnitedStatesCustomary.Foot.nanometerScale
        val inches = feetRemainder.rawValue.nanometers.toDouble(LengthUnit.UnitedStatesCustomary.Inch)
        return action(miles.rawValue, yards.rawValue, feet.rawValue, inches)
    }

    /**
     * Returns the value of this length expressed as a [Double] number of the specified [unit]. Infinite values are
     * converted to either [Double.POSITIVE_INFINITY] or [Double.NEGATIVE_INFINITY] depending on its sign.
     */
    public fun toDouble(unit: LengthUnit): Double {
        return this / unit.nanometerScale.nanometers
    }

    /**
     * Returns a fractional string representation of this length expressed in the specified [unit] and is rounded
     * to the specified [decimals].
     */
    public fun toString(unit: LengthUnit, decimals: Int = 0): String = when (rawNanometers.isInfinite()) {
        true -> rawNanometers.toString()
        false -> buildString {
            append(toDouble(unit).toDecimalString(decimals))
            append(unit.symbol)
        }
    }

    /**
     * Returns a fractional string representation of this energy expressed in the largest [LengthUnit.International]
     * quantity which is greater than or equal to 1.
     */
    public override fun toString(): String {
        val largestUnit = LengthUnit.International.entries.asReversed().firstOrNull { unit ->
            rawNanometers.absoluteValue / unit.nanometerScale > 0
        }
        return toString(largestUnit ?: LengthUnit.International.Nanometer, decimals = 2)
    }

    // endregion

    // region Comparisons

    /**
     * Returns true if this area value is infinite.
     */
    public fun isInfinite(): Boolean = rawNanometers.isInfinite()

    /**
     * Returns true if this area value is finite.
     */
    public fun isFinite(): Boolean = rawNanometers.isFinite()

    /**
     * Compares this length with the [other] length. Returns zero if this length is equal
     * to the specified [other] length, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    public override fun compareTo(other: Length): Int {
        return rawNanometers.compareTo(other.rawNanometers)
    }

    // endregion
}

// region Scalar to Length Conversions

/**
 * Returns a [Length] equal to [Int] number of nanometers.
 */
public inline val Int.nanometers: Length get() = toLength(LengthUnit.International.Nanometer)

/**
 * Returns a [Length] equal to [Long] number of nanometers.
 */
public inline val Long.nanometers: Length get() = toLength(LengthUnit.International.Nanometer)

internal inline val SaturatingLong.nanometers: Length get() = Length(this)

/**
 * Returns a [Length] equal to [Int] number of micrometers.
 */
public inline val Int.micrometers: Length get() = toLength(LengthUnit.International.Micrometer)

/**
 * Returns a [Length] equal to [Long] number of micrometers.
 */
public inline val Long.micrometers: Length get() = toLength(LengthUnit.International.Micrometer)

/**
 * Returns a [Length] equal to [Double] number of micrometers. Depending on its magnitude, some precision may be lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.micrometers: Length get() = toLength(LengthUnit.International.Micrometer)

/**
 * Returns a [Length] equal to [Int] number of millimeters.
 */
public inline val Int.millimeters: Length get() = toLength(LengthUnit.International.Millimeter)

/**
 * Returns a [Length] equal to [Long] number of millimeters.
 */
public inline val Long.millimeters: Length get() = toLength(LengthUnit.International.Millimeter)

/**
 * Returns a [Length] equal to [Double] number of millimeters. Depending on its magnitude, some precision may be lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.millimeters: Length get() = toLength(LengthUnit.International.Millimeter)

/**
 * Returns a [Length] equal to [Int] number of centimeters.
 */
public inline val Int.centimeters: Length get() = toLength(LengthUnit.International.Centimeter)

/**
 * Returns a [Length] equal to [Long] number of centimeters.
 */
public inline val Long.centimeters: Length get() = toLength(LengthUnit.International.Centimeter)

/**
 * Returns a [Length] equal to [Double] number of centimeters. Depending on its magnitude, some precision may be lost.
 */
public inline val Double.centimeters: Length get() = toLength(LengthUnit.International.Centimeter)

/**
 * Returns a [Length] equal to [Int] number of meters.
 */
public inline val Int.meters: Length get() = toLength(LengthUnit.International.Meter)

/**
 * Returns a [Length] equal to [Long] number of meters.
 */
public inline val Long.meters: Length get() = toLength(LengthUnit.International.Meter)

/**
 * Returns a [Length] equal to [Double] number of meters. Depending on its magnitude, some precision may be lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.meters: Length get() = toLength(LengthUnit.International.Meter)

/**
 * Returns a [Length] equal to [Int] number of kilometers.
 */
public inline val Int.kilometers: Length get() = toLength(LengthUnit.International.Kilometer)

/**
 * Returns a [Length] equal to [Long] number of kilometers.
 */
public inline val Long.kilometers: Length get() = toLength(LengthUnit.International.Kilometer)

/**
 * Returns a [Length] equal to [Double] number of kilometers. Depending on its magnitude, some precision may be lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.kilometers: Length get() = toLength(LengthUnit.International.Kilometer)

/**
 * Returns a [Length] equal to [Int] number of megameters.
 */
public inline val Int.megameters: Length get() = toLength(LengthUnit.International.Megameter)

/**
 * Returns a [Length] equal to [Long] number of megameters.
 */
public inline val Long.megameters: Length get() = toLength(LengthUnit.International.Megameter)

/**
 * Returns a [Length] equal to [Double] number of megameters. Depending on its magnitude, some precision may be lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.megameters: Length get() = toLength(LengthUnit.International.Megameter)

/**
 * Returns a [Length] equal to [Int] number of gigameters.
 */
public inline val Int.gigameters: Length get() = toLength(LengthUnit.International.Gigameter)

/**
 * Returns a [Length] equal to [Long] number of gigameters.
 */
public inline val Long.gigameters: Length get() = toLength(LengthUnit.International.Gigameter)

/**
 * Returns a [Length] equal to [Double] number of gigameters. Depending on its magnitude, some precision may be lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.gigameters: Length get() = toLength(LengthUnit.International.Gigameter)

/**
 * Returns a [Length] equal to [Int] number of inches.
 */
public inline val Int.inches: Length get() = toLength(LengthUnit.UnitedStatesCustomary.Inch)

/**
 * Returns a [Length] equal to [Long] number of inches.
 */
public inline val Long.inches: Length get() = toLength(LengthUnit.UnitedStatesCustomary.Inch)

/**
 * Returns a [Length] equal to [Double] number of inches. Depending on its magnitude, some precision may be lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.inches: Length get() = toLength(LengthUnit.UnitedStatesCustomary.Inch)

/**
 * Returns a [Length] equal to [Int] number of feet.
 */
public inline val Int.feet: Length get() = toLength(LengthUnit.UnitedStatesCustomary.Foot)

/**
 * Returns a [Length] equal to [Long] number of feet.
 */
public inline val Long.feet: Length get() = toLength(LengthUnit.UnitedStatesCustomary.Foot)

/**
 * Returns a [Length] equal to [Double] number of feet. Depending on its magnitude, some precision may be lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.feet: Length get() = toLength(LengthUnit.UnitedStatesCustomary.Foot)

/**
 * Returns a [Length] equal to [Int] number of yards.
 */
public inline val Int.yards: Length get() = toLength(LengthUnit.UnitedStatesCustomary.Yard)

/**
 * Returns a [Length] equal to [Long] number of yards.
 */
public inline val Long.yards: Length get() = toLength(LengthUnit.UnitedStatesCustomary.Yard)

/**
 * Returns a [Length] equal to [Double] number of yards. Depending on its magnitude, some precision may be lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.yards: Length get() = toLength(LengthUnit.UnitedStatesCustomary.Yard)

/**
 * Returns a [Length] equal to [Int] number of miles.
 */
public inline val Int.miles: Length get() = toLength(LengthUnit.UnitedStatesCustomary.Mile)

/**
 * Returns a [Length] equal to [Long] number of miles.
 */
public inline val Long.miles: Length get() = toLength(LengthUnit.UnitedStatesCustomary.Mile)

/**
 * Returns a [Length] equal to [Double] number of miles. Depending on its magnitude, some precision may be lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.miles: Length get() = toLength(LengthUnit.UnitedStatesCustomary.Mile)

/**
 * Returns a [Length] equal to [Int] number of the specified [unit].
 */
public fun Int.toLength(unit: LengthUnit): Length {
    return toLong().toLength(unit)
}

/**
 * Returns a [Length] equal to [Long] number of the specified [unit].
 */
public fun Long.toLength(unit: LengthUnit): Length {
    return Length(this.saturated * unit.nanometerScale)
}

/**
 * Returns a [Length] equal to [Double] number of the specified [unit]. Depending on its magnitude, some precision may
 * be lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public fun Double.toLength(unit: LengthUnit): Length {
    val valueInNanometers = this * unit.nanometerScale
    require(!valueInNanometers.isNaN()) { "Length value cannot be NaN." }
    return Length(valueInNanometers.roundToLong().saturated)
}

// endregions

/**
 * A unit of length precise to the nanometer.
 */
public interface LengthUnit {

    /**
     * The amount of nanometers in this unit. Implementations of [LengthUnit] should be perfectly divisible by a
     * quantity of nanometers.
     */
    public val nanometerScale: Long

    /**
     * The symbol of this unit.
     */
    public val symbol: String

    public enum class International(
        override val nanometerScale: Long,
        override val symbol: String,
    ) : LengthUnit {
        Nanometer(1, "nm"),
        Micrometer(1_000, "μm"),
        Millimeter(1_000_000, "mm"),
        Centimeter(10_000_000, "cm"),
        Meter(1_000_000_000, "m"),
        Kilometer(1_000_000_000_000, "km"),
        Megameter(1_000_000_000_000_000, "Mm"),
        Gigameter(1_000_000_000_000_000_000, "Gm"),
    }

    public enum class UnitedStatesCustomary(
        override val nanometerScale: Long,
        override val symbol: String,
    ) : LengthUnit {
        Inch(25_400_000, "in"),
        Foot(304_800_000, "ft"),
        Yard(914_400_000, "yd"),
        Mile(1_609_344_000_000, "mi"),
    }
}