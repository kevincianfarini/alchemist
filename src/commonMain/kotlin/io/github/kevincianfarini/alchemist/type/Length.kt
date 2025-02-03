package io.github.kevincianfarini.alchemist.type

import io.github.kevincianfarini.alchemist.internal.POSITIVE_INFINITY
import io.github.kevincianfarini.alchemist.internal.SaturatingLong
import io.github.kevincianfarini.alchemist.internal.isPreciseToNanosecond
import io.github.kevincianfarini.alchemist.internal.saturated
import io.github.kevincianfarini.alchemist.internal.sign
import io.github.kevincianfarini.alchemist.internal.throwIllegalArgumentException
import io.github.kevincianfarini.alchemist.internal.toDecimalString
import io.github.kevincianfarini.alchemist.scalar.nanometers
import io.github.kevincianfarini.alchemist.scalar.nmPerSecond
import io.github.kevincianfarini.alchemist.unit.LengthUnit
import io.github.kevincianfarini.alchemist.unit.LengthUnit.International.Nanometer
import kotlin.jvm.JvmInline
import kotlin.math.roundToInt
import kotlin.math.roundToLong
import kotlin.time.Duration
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toDuration

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
            throwIllegalArgumentException("Dividing two infinite values yields an undefined result.")
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
            throwIllegalArgumentException("Dividing two infinite values yields an undefined result.")
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
     * @throws IllegalArgumentException when this length is [infinite][isInfinite] and [other] is 0 or vice versa.
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
     * Returns the resulting [Volume] after applying this length over the specified [area].
     *
     * This operation attempts to retain precision, but for sufficiently large values of this length or the
     * specified [area] some precision may be lost.
     *
     * @throws IllegalArgumentException if this length is [infinite][isInfinite] and [area] is zero, or if this length
     * is zero and [area] is infinite.
     */
    public operator fun times(area: Area): Volume = area * this

    /**
     * Returns the amount of [Energy] required to apply the specified [force] over this length.
     *
     * This operation attempts to retain precision, but for sufficiently large values of either this length or the
     * specified [force], some precision may be lost.
     *
     * @throws IllegalArgumentException when this length is [infinite][isInfinite] and [force] is 0 or vice versa.
     */
    public operator fun times(force: Force): Energy = force * this

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
     * Returns the negative of this length value.
     */
    public operator fun unaryMinus(): Length = Length(-rawNanometers)

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

    /**
     * Returns a length whose value is multiplied by the specified [scale]. This operation may be rounded when the result
     * cannot be precisely represented with a [Double] number.
     *
     * @throws IllegalArgumentException when this length is [infinite][isInfinite] and [scale] is 0.0 or when this length is 0
     * and scale is [infinite][Double.isInfinite].
     */
    public operator fun times(scale: Double): Length = Length(rawNanometers * scale)

    /**
     * Returns a length whose value is divided by the specified [scale]. This operation may be rounded when the result
     * cannot be precisely represented with a [Double] number.
     *
     * @throws IllegalArgumentException when this length is [infinite][isInfinite] and [scale] is 0.0 or when this length is 0
     * and scale is [infinite][Double.isInfinite].
     */
    public operator fun div(scale: Double): Length = Length(rawNanometers / scale)

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
     * Returns the value of this length expressed as a [Long] number of the specified [unit]. Infinite values are
     * converted to either [Long.MAX_VALUE] or [Long.MIN_VALUE] depending on its sign.
     */
    public fun toLong(unit: LengthUnit): Long {
        return (rawNanometers / unit.nanometerScale).rawValue
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
     * Returns a fractional string representation of this length expressed in the largest [LengthUnit.International]
     * quantity which is greater than or equal to 1.
     */
    public override fun toString(): String {
        val largestUnit = LengthUnit.International.entries.asReversed().firstOrNull { unit ->
            rawNanometers.absoluteValue / unit.nanometerScale > 0
        }
        return toString(largestUnit ?: Nanometer, decimals = 2)
    }

    // endregion

    // region Comparisons

    /**
     * Returns true if this length value is infinite.
     */
    public fun isInfinite(): Boolean = rawNanometers.isInfinite()

    /**
     * Returns true if this length value is finite.
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
