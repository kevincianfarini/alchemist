package io.github.kevincianfarini.alchemist

import io.github.kevincianfarini.alchemist.internal.SaturatingLong
import io.github.kevincianfarini.alchemist.internal.isPreciseToNanosecond
import io.github.kevincianfarini.alchemist.internal.saturated
import io.github.kevincianfarini.alchemist.internal.toDecimalString
import kotlin.jvm.JvmInline
import kotlin.math.roundToLong
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
    public operator fun div(length: Length): Force = TODO()

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
            throw IllegalArgumentException("Dividing two infinite values yields an undefined result.")
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
     * Returns the value of this area expressed as a [Double] number of the specified [unit]. Infinite values are
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

// region Scalar to Energy Conversions

/**
 * Returns an [Energy] equal to [Int] number of millijoules.
 */
public inline val Int.millijoules: Energy get() = toEnergy(EnergyUnit.International.Millijoule)

/**
 * Returns an [Energy] equal to [Long] number of millijoules.
 */
public inline val Long.millijoules: Energy get() = toEnergy(EnergyUnit.International.Millijoule)
internal inline val SaturatingLong.millijoules get() = Energy(this)

/**
 * Returns an [Energy] equal to [Int] number of joules.
 */
public inline val Int.joules: Energy get() = toEnergy(EnergyUnit.International.Joule)

/**
 * Returns an [Energy] equal to [Long] number of joules.
 */
public inline val Long.joules: Energy get() = toEnergy(EnergyUnit.International.Joule)

/**
 * Returns an [Energy] equal to [Double] number of joules. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.joules: Energy get() = toEnergy(EnergyUnit.International.Joule)

/**
 * Returns an [Energy] equal to [Int] number of kilojoules.
 */
public inline val Int.kilojoules: Energy get() = toEnergy(EnergyUnit.International.Kilojoule)

/**
 * Returns an [Energy] equal to [Long] number of kilojoules.
 */
public inline val Long.kilojoules: Energy get() = toEnergy(EnergyUnit.International.Kilojoule)

/**
 * Returns an [Energy] equal to [Double] number of kilojoules. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.kilojoules: Energy get() = toEnergy(EnergyUnit.International.Kilojoule)

/**
 * Returns an [Energy] equal to [Int] number of megajoules.
 */
public inline val Int.megajoules: Energy get() = toEnergy(EnergyUnit.International.Megajoule)

/**
 * Returns an [Energy] equal to [Long] number of megajoules.
 */
public inline val Long.megajoules: Energy get() = toEnergy(EnergyUnit.International.Megajoule)

/**
 * Returns an [Energy] equal to [Double] number of megajoules. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.megajoules: Energy get() = toEnergy(EnergyUnit.International.Megajoule)

/**
 * Returns an [Energy] equal to [Int] number of gigajoules.
 */
public inline val Int.gigajoules: Energy get() = toEnergy(EnergyUnit.International.Gigajoule)

/**
 * Returns an [Energy] equal to [Long] number of gigajoules.
 */
public inline val Long.gigajoules: Energy get() = toEnergy(EnergyUnit.International.Gigajoule)

/**
 * Returns an [Energy] equal to [Double] number of gigajoules. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.gigajoules: Energy get() = toEnergy(EnergyUnit.International.Gigajoule)

/**
 * Returns an [Energy] equal to [Int] number of tetrajoules.
 */
public inline val Int.tetrajoules: Energy get() = toEnergy(EnergyUnit.International.Tetrajoule)

/**
 * Returns an [Energy] equal to [Long] number of tetrajoules.
 */
public inline val Long.tetrajoules: Energy get() = toEnergy(EnergyUnit.International.Tetrajoule)

/**
 * Returns an [Energy] equal to [Double] number of tetrajoules. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.tetrajoules: Energy get() = toEnergy(EnergyUnit.International.Tetrajoule)

/**
 * Returns an [Energy] equal to [Int] number of petajoules.
 */
public inline val Int.petajoules: Energy get() = toEnergy(EnergyUnit.International.Petajoule)

/**
 * Returns an [Energy] equal to [Long] number of petajoules.
 */
public inline val Long.petajoules: Energy get() = toEnergy(EnergyUnit.International.Petajoule)

/**
 * Returns an [Energy] equal to [Double] number of petajoules. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.petajoules: Energy get() = toEnergy(EnergyUnit.International.Petajoule)

/**
 * Returns an [Energy] equal to [Int] number of milliwatt-hours.
 */
public inline val Int.milliwattHours: Energy get() = toEnergy(EnergyUnit.Electricity.MilliwattHour)

/**
 * Returns an [Energy] equal to [Long] number of milliwatt-hours.
 */
public inline val Long.milliwattHours: Energy get() = toEnergy(EnergyUnit.Electricity.MilliwattHour)

/**
 * Returns an [Energy] equal to [Double] number of milliwatt-hours. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.milliwattHours: Energy get() = toEnergy(EnergyUnit.Electricity.MilliwattHour)

/**
 * Returns an [Energy] equal to [Int] number of watt-hours.
 */
public inline val Int.wattHours: Energy get() = toEnergy(EnergyUnit.Electricity.WattHour)

/**
 * Returns an [Energy] equal to [Long] number of watt-hours.
 */
public inline val Long.wattHours: Energy get() = toEnergy(EnergyUnit.Electricity.WattHour)

/**
 * Returns an [Energy] equal to [Double] number of watt-hours. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.wattHours: Energy get() = toEnergy(EnergyUnit.Electricity.WattHour)

/**
 * Returns an [Energy] equal to [Int] number of kilowatt-hours.
 */
public inline val Int.kilowattHours: Energy get() = toEnergy(EnergyUnit.Electricity.KilowattHour)

/**
 * Returns an [Energy] equal to [Long] number of kilowatt-hours.
 */
public inline val Long.kilowattHours: Energy get() = toEnergy(EnergyUnit.Electricity.KilowattHour)

/**
 * Returns an [Energy] equal to [Double] number of kilowatt-hours. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.kilowattHours: Energy get() = toEnergy(EnergyUnit.Electricity.KilowattHour)

/**
 * Returns an [Energy] equal to [Int] number of megawatt-hours.
 */
public inline val Int.megawattHours: Energy get() = toEnergy(EnergyUnit.Electricity.MegawattHour)

/**
 * Returns an [Energy] equal to [Long] number of megawatt-hours.
 */
public inline val Long.megawattHours: Energy get() = toEnergy(EnergyUnit.Electricity.MegawattHour)

/**
 * Returns an [Energy] equal to [Double] number of megawatt-hours. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.megawattHours: Energy get() = toEnergy(EnergyUnit.Electricity.MegawattHour)

/**
 * Returns an [Energy] equal to [Int] number of gigawatt-hours.
 */
public inline val Int.gigawattHours: Energy get() = toEnergy(EnergyUnit.Electricity.GigawattHour)

/**
 * Returns an [Energy] equal to [Long] number of gigawatt-hours.
 */
public inline val Long.gigawattHours: Energy get() = toEnergy(EnergyUnit.Electricity.GigawattHour)

/**
 * Returns an [Energy] equal to [Double] number of gigawatt-hours. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.gigawattHours: Energy get() = toEnergy(EnergyUnit.Electricity.GigawattHour)

/**
 * Returns an [Energy] equal to [Int] number of terawatt-hours.
 */
public inline val Int.terawattHours: Energy get() = toEnergy(EnergyUnit.Electricity.TerawattHour)

/**
 * Returns an [Energy] equal to [Int] number of terawatt-hours.
 */
public inline val Long.terawattHours: Energy get() = toEnergy(EnergyUnit.Electricity.TerawattHour)

/**
 * Returns an [Energy] equal to [Double] number of terawatt-hours. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.terawattHours: Energy get() = toEnergy(EnergyUnit.Electricity.TerawattHour)

/**
 * Returns an [Energy] equal to [Int] number of the specified [unit].
 */
public fun Int.toEnergy(unit: EnergyUnit): Energy {
    return toLong().toEnergy(unit)
}

/**
 * Returns an [Energy] equal to [Long] number of the specified [unit].
 */
public fun Long.toEnergy(unit: EnergyUnit): Energy {
    return Energy(this.saturated * unit.millijouleScale)
}

/**
 * Returns an [Energy] equal to [Double] number of the specified [unit]. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public fun Double.toEnergy(unit: EnergyUnit): Energy {
    val valueInMillijoules = this * unit.millijouleScale
    require(!valueInMillijoules.isNaN()) { "Energy value cannot be NaN." }
    return Energy(valueInMillijoules.roundToLong().saturated)
}

// endregion

/**
 * A unit of energy precise to the millijoule.
 */
public interface EnergyUnit {

    /**
     * The amount of millijoules in this unit. Implementations of [EnergyUnit] should be perfectly divisible by a
     * quantity of millijoules.
     */
    public val millijouleScale: Long

    /**
     * The symbol of this unit.
     */
    public val symbol: String

    public enum class International(
        override val millijouleScale: Long,
        override val symbol: String,
    ) : EnergyUnit {
        Millijoule(1, "mJ"),
        Joule(1_000, "J"),
        Kilojoule(1_000_000, "kJ"),
        Megajoule(1_000_000_000, "MJ"),
        Gigajoule(1_000_000_000_000, "GJ"),
        Tetrajoule(1_000_000_000_000_000, "TJ"),
        Petajoule(1_000_000_000_000_000_000, "PJ"),
    }

    public enum class Electricity(
        override val millijouleScale: Long,
        override val symbol: String,
    ) : EnergyUnit {
        MilliwattHour(3_600, "mWh"),
        WattHour(3_600_000, "Wh"),
        KilowattHour(3_600_000_000, "kWh"),
        MegawattHour(3_600_000_000_000, "MWh"),
        GigawattHour(3_600_000_000_000_000, "GWh"),
        TerawattHour(3_600_000_000_000_000_000, "TWh"),
    }
}
