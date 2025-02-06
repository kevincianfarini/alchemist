package io.github.kevincianfarini.alchemist.type

import io.github.kevincianfarini.alchemist.internal.SaturatingLong
import io.github.kevincianfarini.alchemist.internal.isPreciseToNanosecond
import io.github.kevincianfarini.alchemist.internal.saturated
import io.github.kevincianfarini.alchemist.internal.throwIllegalArgumentException
import io.github.kevincianfarini.alchemist.internal.toDecimalString
import io.github.kevincianfarini.alchemist.scalar.kilonewtons
import io.github.kevincianfarini.alchemist.scalar.kilowatts
import io.github.kevincianfarini.alchemist.scalar.megawatts
import io.github.kevincianfarini.alchemist.scalar.microwatts
import io.github.kevincianfarini.alchemist.scalar.millijoules
import io.github.kevincianfarini.alchemist.scalar.milliwatts
import io.github.kevincianfarini.alchemist.scalar.watts
import io.github.kevincianfarini.alchemist.unit.EnergyUnit
import kotlin.jvm.JvmInline
import kotlin.math.roundToInt
import kotlin.time.Duration

/**
 * Represents an amount of energy and is capable of storing ±9.2 petajoules or ±2.56 terawatt-hours at millijoule
 * precision.
 */
@JvmInline
public value class Energy internal constructor(private val rawMillijoules: SaturatingLong) : Comparable<Energy> {

    // region SI Arithmetic

    /**
     * Returns the constant [Force] applied over the specified [length] required to expend this amount of energy.
     *
     * This operation attempts to retain precision, but for sufficiently large values of either this energy or the
     * other [length], some precision may be lost.
     *
     * @throws IllegalArgumentException if both this energy and [length] are infinite.
     */
    public operator fun div(length: Length): Force {
        // TODO This is simplistic and we should attempt to retain precision in the future.
        return ((rawMillijoules / length.rawNanometers) * 1_000).kilonewtons
    }

    /**
     * Returns the constant [Power] applied over the specified [duration] to generate this amount of energy.
     *
     * This operation attempts to retain precision, but for sufficiently large values of either this energy or the
     * other [duration], some precision may be lost.
     *
     * @throws IllegalArgumentException if both this energy and [duration] are infinite.
     */
    public operator fun div(duration: Duration): Power = when {
        rawMillijoules.isInfinite() && duration.isInfinite() -> {
            throwIllegalArgumentException("Dividing two infinite values yields an undefined result.")
        }
        rawMillijoules.isInfinite() -> Power(rawMillijoules)
        duration.isInfinite() -> Power(0L.saturated)
        else -> calculatePower(duration)
    }

    private fun calculatePower(duration: Duration): Power {
        // Try to find the right level which we can perform this operation at without losing precision.
        if (duration.isPreciseToNanosecond()) {
            val power = millijoulesPerNs(rawMillijoules, duration.inWholeNanoseconds)
            if (power.isFinite()) return power
        }
        val ms = duration.inWholeMilliseconds
        val power = millijoulesPerMs(rawMillijoules, ms)
        if (power.isFinite()) return power
        return (rawMillijoules / ms).watts
    }

    private fun millijoulesPerMs(millijoules: SaturatingLong, ms: Long): Power {
        // 1 millijoule per 1 millisecond is 1 watt.
        val watts = (millijoules / ms).watts
        return watts + microjoulesPerMs((millijoules % ms) * 1_000, ms)
    }

    private fun microjoulesPerMs(microjoules: SaturatingLong, ms: Long): Power {
        // 1 microjoule per 1 millisecond is 1 milliwatt.
        val milliwatts = (microjoules / ms).milliwatts
        return milliwatts + nanojoulesPerMs((microjoules % ms) * 1_000, ms)
    }

    private fun nanojoulesPerMs(nanojoules: SaturatingLong, ms: Long): Power {
        // 1 nanojoule per 1 millisecond is 1 microwatt.
        return (nanojoules / ms).microwatts
    }

    private fun millijoulesPerNs(millijoules: SaturatingLong, ns: Long): Power {
        // 1 millijoule per 1 nanosecond is 1 megawatt.
        val megawatts = (millijoules / ns).megawatts
        return megawatts + microjoulesPerNs((millijoules % ns) * 1_000, ns)
    }

    private fun microjoulesPerNs(microjoules: SaturatingLong, ns: Long): Power {
        // 1 microjoule per 1 nanosecond is 1 kilowatt.
        val kilowatts = (microjoules / ns).kilowatts
        return kilowatts + nanojoulesPerNs((microjoules % ns) * 1_000, ns)
    }

    private fun nanojoulesPerNs(nanojoules: SaturatingLong, ns: Long): Power {
        // 1 nanojoule per 1 nanosecond is 1 watt.
        val watts = (nanojoules / ns).watts
        return watts + picojoulesPerNs((nanojoules % ns) * 1_000, ns)
    }

    private fun picojoulesPerNs(picojoules: SaturatingLong, ns: Long): Power {
        // 1 picojoule per 1 nanosecond is 1 milliwatt.
        val milliwatts = (picojoules / ns).milliwatts
        return milliwatts + femtojoulesPerNs((picojoules % ns) * 1_000, ns)
    }

    private fun femtojoulesPerNs(femtojoules: SaturatingLong, ns: Long): Power {
        // 1 femtojoule per 1 nanosecond is 1 microwatt.
        return (femtojoules / ns).microwatts
    }

    // endregion

    // region Scalar Arithmetic

    /**
     * Returns the number that is the ratio of this and the [other] energy value.
     *
     * @throws IllegalArgumentException when both this and the [other] energy are [infinite][isInfinite].
     */
    public operator fun div(other: Energy): Double {
        return rawMillijoules.toDouble() / other.rawMillijoules.toDouble()
    }

    /**
     * Returns an energy whose value is this energy value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Energy {
        return div(scale.toLong())
    }

    /**
     * Returns an energy whose value is this energy value divided by the specified [scale].
     *
     * @throws IllegalArgumentException when [scale] is equal to [Long.MAX_VALUE] or [Long.MIN_VALUE] and this
     * energy is [infinite][isInfinite].
     */
    public operator fun div(scale: Long): Energy {
        return Energy(rawMillijoules / scale)
    }

    /**
     * Returns the negative of this energy value.
     */
    public operator fun unaryMinus(): Energy = Energy(-rawMillijoules)

    /**
     * Returns an energy whose value is the difference between this and the [other] energy value.
     *
     * @throws IllegalArgumentException if this energy and the [other] energy are both
     * [infinite][isInfinite] but have equivalent signs.
     */
    public operator fun minus(other: Energy): Energy {
        return Energy(rawMillijoules - other.rawMillijoules)
    }

    /**
     * Returns an energy whose value is the sum between this and the [other] energy value.
     *
     * @throws IllegalArgumentException if this energy and the [other] energy are both
     * [infinite][isInfinite] but have differing signs.
     */
    public operator fun plus(other: Energy): Energy {
        return Energy(rawMillijoules + other.rawMillijoules)
    }

    /**
     * Returns an energy whose value is multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this energy is [infinite][isInfinite] and [scale] is 0.
     */
    public operator fun times(scale: Int): Energy {
        return div(scale.toLong())
    }

    /**
     * Returns an energy whose value is multiplied by the specified [scale].
     *
     * @throws IllegalArgumentException when this energy is [infinite][isInfinite] and [scale] is 0, or when this
     * energy is 0 and scale is [Long.MAX_VALUE] or [Long.MIN_VALUE].
     */
    public operator fun times(scale: Long): Energy {
        return Energy(rawMillijoules * scale)
    }

    /**
     * Returns an energy whose value is multiplied by the specified [scale]. This operation may be rounded when the result
     * cannot be precisely represented with a [Double] number.
     *
     * @throws IllegalArgumentException when this energy is [infinite][isInfinite] and [scale] is 0.0 or when this energy is 0
     * and scale is [infinite][Double.isInfinite].
     */
    public operator fun times(scale: Double): Energy = Energy(rawMillijoules * scale)

    /**
     * Returns an energy whose value is divided by the specified [scale]. This operation may be rounded when the result
     * cannot be precisely represented with a [Double] number.
     *
     * @throws IllegalArgumentException when this energy is [infinite][isInfinite] and [scale] is 0.0 or when this energy is 0
     * and scale is [infinite][Double.isInfinite].
     */
    public operator fun div(scale: Double): Energy = Energy(rawMillijoules / scale)

    // endregion

    // region Energy to Scalar Conversions

    /**
     * Splits this energy into petajoules, tetrajoules, gigajoules, megajoules, kilojoules, joules, and millijoules
     * and executes the [action] with those components. The result of [action] is returned as the result of this
     * function.
     *
     * Infinite energy values invoke [action] with [Long.MAX_VALUE] or [Long.MIN_VALUE] for every component, depending
     * on the infinite value's sign.
     */
    public fun <T> toInternationalComponents(
        action: (
            petajoules: Long,
            tetrajoules: Long,
            gigajoules: Long,
            megajoules: Long,
            kilojoules: Long,
            joules: Long,
            millijoules: Long,
        ) -> T
    ): T {
        val peta = rawMillijoules / EnergyUnit.International.Petajoule.millijouleScale
        val petaRemainder = rawMillijoules % EnergyUnit.International.Petajoule.millijouleScale
        val tetra = petaRemainder / EnergyUnit.International.Tetrajoule.millijouleScale
        val tetraRemainder = petaRemainder % EnergyUnit.International.Tetrajoule.millijouleScale
        val giga = tetraRemainder / EnergyUnit.International.Gigajoule.millijouleScale
        val gigaRemainder = tetraRemainder % EnergyUnit.International.Gigajoule.millijouleScale
        val mega = gigaRemainder / EnergyUnit.International.Megajoule.millijouleScale
        val megaRemainder = gigaRemainder % EnergyUnit.International.Megajoule.millijouleScale
        val kilo = megaRemainder / EnergyUnit.International.Kilojoule.millijouleScale
        val kiloRemainder = megaRemainder % EnergyUnit.International.Kilojoule.millijouleScale
        val joule = kiloRemainder / EnergyUnit.International.Joule.millijouleScale
        val milliJoule = kiloRemainder % EnergyUnit.International.Joule.millijouleScale
        return action(
            peta.rawValue,
            tetra.rawValue,
            giga.rawValue,
            mega.rawValue,
            kilo.rawValue,
            joule.rawValue,
            milliJoule.rawValue,
        )
    }

    /**
     * Splits this energy into terawatt-hours, gigawatt-hours, megawatt-hours, kilowatt-hours, watt-hours,
     * milliwatt-hours, and microwatt-hours and executes the [action] with those components. The result of [action] is
     * returned as the result of this function.
     *
     * Infinite energy values invoke [action] with [Long.MAX_VALUE], [Long.MIN_VALUE], [Double.POSITIVE_INFINITY], or
     * [Double.NEGATIVE_INFINITY] for every component, depending on the infinite value's sign and the component's type.
     */
    public fun <T> toElectricityComponents(
        action: (
            terawattHours: Long,
            gigawattHours: Long,
            megawattHours: Long,
            kilowattHours: Long,
            wattHours: Long,
            milliwattHours: Long,
            microwattHours: Double,
        ) -> T
    ): T {
        val tera = rawMillijoules / EnergyUnit.Electricity.TerawattHour.millijouleScale
        val teraRemainder = rawMillijoules % EnergyUnit.Electricity.TerawattHour.millijouleScale
        val giga = teraRemainder / EnergyUnit.Electricity.GigawattHour.millijouleScale
        val gigaRemainder = teraRemainder % EnergyUnit.Electricity.GigawattHour.millijouleScale
        val mega = gigaRemainder / EnergyUnit.Electricity.MegawattHour.millijouleScale
        val megaRemainder = gigaRemainder % EnergyUnit.Electricity.MegawattHour.millijouleScale
        val kilo = megaRemainder / EnergyUnit.Electricity.KilowattHour.millijouleScale
        val kiloRemainder = megaRemainder % EnergyUnit.Electricity.KilowattHour.millijouleScale
        val wattHour = kiloRemainder / EnergyUnit.Electricity.WattHour.millijouleScale
        val wattRemainder = kiloRemainder % EnergyUnit.Electricity.WattHour.millijouleScale
        val milliwattHour = wattRemainder / EnergyUnit.Electricity.MilliwattHour.millijouleScale
        val milliwattRemainder = wattRemainder % EnergyUnit.Electricity.MilliwattHour.millijouleScale
        val microwattHours = milliwattRemainder.toDouble() / 3.6 // 3.6 millijoules per microwatt-hour.
        return action(
            tera.rawValue,
            giga.rawValue,
            mega.rawValue,
            kilo.rawValue,
            wattHour.rawValue,
            milliwattHour.rawValue,
            microwattHours,
        )
    }

    /**
     * Returns the value of this energy expressed as a [Long] number of the specified [unit]. Infinite values are
     * converted to either [Long.MAX_VALUE] or [Long.MIN_VALUE] depending on its sign.
     */
    public fun toLong(unit: EnergyUnit): Long {
        return (rawMillijoules / unit.millijouleScale).rawValue
    }

    /**
     * Returns the value of this energy expressed as a [Double] number of the specified [unit]. Infinite values are
     * converted to either [Double.POSITIVE_INFINITY] or [Double.NEGATIVE_INFINITY] depending on its sign.
     */
    public fun toDouble(unit: EnergyUnit): Double {
        return this / unit.millijouleScale.millijoules
    }

    /**
     * Returns a fractional string representation of this energy expressed in the specified [EnergyUnit] and is rounded
     * to the specified [decimals].
     */
    public fun toString(unit: EnergyUnit, decimals: Int = 0): String = when (rawMillijoules.isInfinite()) {
        true -> rawMillijoules.toString()
        false -> buildString {
            append(toDouble(unit).toDecimalString(decimals))
            append(unit.symbol)
        }
    }

    /**
     * Returns a fractional string representation of this energy expressed in the largest [EnergyUnit.International]
     * quantity which is greater than or equal to 1.
     */
    public override fun toString(): String {
        val largestUnit = EnergyUnit.International.entries.asReversed().firstOrNull { unit ->
            rawMillijoules.absoluteValue / unit.millijouleScale > 0
        }
        return toString(largestUnit ?: EnergyUnit.International.Millijoule, decimals = 2)
    }

    // endregion

    // region Comparisons

    /**
     * Returns true if this area value is infinite.
     */
    public fun isInfinite(): Boolean = rawMillijoules.isInfinite()

    /**
     * Returns true if this area value is finite.
     */
    public fun isFinite(): Boolean = rawMillijoules.isFinite()

    /**
     * Compares this energy with the [other] energy. Returns zero if this energy is equal
     * to the specified [other] energy, a negative number if it's less than [other], or a positive number
     * if it's greater than [other].
     */
    public override fun compareTo(other: Energy): Int {
        return rawMillijoules.compareTo(other.rawMillijoules)
    }

    // endregion
}
