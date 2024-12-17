package io.github.kevincianfarini.alchemist

import io.github.kevincianfarini.alchemist.OverflowLong.Companion.noOverflow
import io.github.kevincianfarini.alchemist.Power.Companion.kilowatts
import io.github.kevincianfarini.alchemist.Power.Companion.megawatts
import io.github.kevincianfarini.alchemist.Power.Companion.microwatts
import io.github.kevincianfarini.alchemist.Power.Companion.milliwatts
import io.github.kevincianfarini.alchemist.Power.Companion.watts
import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

/**
 * Represents an amount of energy and is capable of storing ±9.2 petajoules or ±2.56 terawatt-hours at millijoule
 * precision.
 */
@JvmInline
public value class Energy internal constructor(private val rawMillijoules: OverflowLong) {

    /**
     * Returns the constant [Force] applied over the specified [distance] required to expend this amount of energy.
     */
    public operator fun div(distance: Distance): Force = TODO()

    /**
     * Returns the constant [Power] applied over the specified [duration] to generate this amount of energy.
     *
     * This operation attempts to retain precision, but for sufficiently large values of either this energy or the
     * other [duration], some precision may be lost.
     *
     * @throws IllegalArgumentException if both this energy and [duration] are infinite.
     */
    public operator fun div(duration: Duration): Power {
        // Try to find the right level which we can perform this operation at without losing precision.
        //  -------------------------------------------------------------------------------------------
        // 1 femtojoule per 1 nanosecond is 1 microwatt.
        // 1 picojoule per 1 nanosecond is 1 milliwatt.
        // 1 nanojoule per 1 nanosecond is 1 watt.
        // 1 microjoule per 1 nanosecond is 1 kilowatt.
        // 1 millijoule per 1 nanosecond is 1 megawatt.
        // --------------------------------------------------------------------------------------------
        // 1 nanojoule per 1 millisecond is 1 microwatt.
        // 1 microjoule per 1 millisecond is 1 milliwatt.
        // 1 millijoule per 1 millisecond is 1 watt.
        // --------------------------------------------------------------------------------------------
        val microjoules = rawMillijoules * 1_000
        val nanojoules = microjoules * 1_000
        val picojoules = nanojoules * 1_000
        val femtojoules = picojoules * 1_000
        val durationPreciseToNanosecond = duration == duration.inWholeNanoseconds.nanoseconds
        return when {
            rawMillijoules.isInfinite() && duration.isInfinite() -> {
                throw IllegalArgumentException("Dividing two infinite values yields an undefined result.")
            }
            rawMillijoules.isInfinite() -> Power(rawMillijoules)
            duration.isInfinite() -> Power(0L.noOverflow)
            durationPreciseToNanosecond && femtojoules.isFinite() -> {
                (femtojoules / duration.inWholeNanoseconds).microwatts
            }
            durationPreciseToNanosecond && picojoules.isFinite() -> {
                val ns = duration.inWholeNanoseconds
                val us = duration.inWholeMicroseconds
                val milliwatts = (picojoules / ns).milliwatts
                val microwatts = ((picojoules % ns) / us).microwatts
                milliwatts + microwatts
            }
            durationPreciseToNanosecond && nanojoules.isFinite() -> {
                val ns = duration.inWholeNanoseconds
                val us = duration.inWholeMicroseconds
                val ms = duration.inWholeMilliseconds
                val watts = (nanojoules / ns).watts
                val milliwatts = ((nanojoules % ns) / us).milliwatts
                val microwatts = ((nanojoules % us) / ms).microwatts
                watts + milliwatts + microwatts
            }
            durationPreciseToNanosecond && microjoules.isFinite() -> {
                val ns = duration.inWholeNanoseconds
                val us = duration.inWholeMicroseconds
                val ms = duration.inWholeMilliseconds
                val s = duration.inWholeSeconds
                val kilowatts = (microjoules / ns).kilowatts
                val watts = ((microjoules % ns) / us).watts
                val milliwatts = ((microjoules % us) / ms).milliwatts
                val microwatts = ((microjoules % ms) / s).microwatts
                kilowatts + watts + milliwatts + microwatts
            }
            durationPreciseToNanosecond -> {
                val ns = duration.inWholeNanoseconds
                val us = duration.inWholeMicroseconds
                val ms = duration.inWholeMilliseconds
                val s = duration.inWholeSeconds
                val megawatts = (rawMillijoules / ns).megawatts
                val kilowatts = ((rawMillijoules % ns) / us).kilowatts
                val watts = ((rawMillijoules % us) / ms).watts
                val milliwatts = ((rawMillijoules % ms) / s).milliwatts
                val microwatts = ((rawMillijoules % s) / (s / 1_000)).microwatts
                megawatts + kilowatts + watts + milliwatts + microwatts
            }
            nanojoules.isFinite() -> {
                (nanojoules / duration.inWholeMilliseconds).microwatts
            }
            microjoules.isFinite() -> {
                val ms = duration.inWholeMilliseconds
                val s = duration.inWholeSeconds
                val milliwatts = (microjoules / ms).milliwatts
                val microwatts = ((microjoules % ms) / s).microwatts
                milliwatts + microwatts
            }
            else -> {
                val ms = duration.inWholeMilliseconds
                val s = duration.inWholeSeconds
                val watts = (rawMillijoules / ms).watts
                val milliwatts = ((rawMillijoules % ms) / s).milliwatts
                val microwatts = ((rawMillijoules % s) / (s / 1_000)).microwatts
                watts + milliwatts + microwatts
            }
        }
    }

    /**
     * Returns the number that is the ratio of this and the [other] energy value.
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
     */
    public operator fun div(scale: Long): Energy {
        return Energy(rawMillijoules / scale)
    }

    /**
     * Returns an energy whose value is the difference between this and the [other] energy value.
     */
    public operator fun minus(other: Energy): Energy {
        return Energy(rawMillijoules - other.rawMillijoules)
    }

    /**
     * Returns an energy whose value is the sum between this and the [other] energy value.
     */
    public operator fun plus(other: Energy): Energy {
        return Energy(rawMillijoules + other.rawMillijoules)
    }

    /**
     * Returns an energy whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Energy {
        return div(scale.toLong())
    }

    /**
     * Returns an energy whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Energy {
        return Energy(rawMillijoules * scale)
    }

    public fun toDouble(unit: EnergyUnit): Double {
        return this / unit.millijouleScale.millijoules
    }

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

    public fun toString(unit: EnergyUnit): String = when (rawMillijoules.isInfinite()) {
        true -> rawMillijoules.toString()
        false -> "${toDouble(unit)}${unit.symbol}"
    }

    public override fun toString(): String {
        val largestUnit = EnergyUnit.International.entries.asReversed().first { unit ->
            rawMillijoules.absoluteValue / unit.millijouleScale > 0
        }
        return toString(largestUnit)
    }

    public companion object {

        public inline val Int.millijoules: Energy get() = toEnergy(EnergyUnit.International.Millijoule)
        public inline val Long.millijoules: Energy get() = toEnergy(EnergyUnit.International.Millijoule)
        public inline val Int.joules: Energy get() = toEnergy(EnergyUnit.International.Joule)
        public inline val Long.joules: Energy get() = toEnergy(EnergyUnit.International.Joule)
        public inline val Int.kilojoules: Energy get() = toEnergy(EnergyUnit.International.Kilojoule)
        public inline val Long.kilojoules: Energy get() = toEnergy(EnergyUnit.International.Kilojoule)
        public inline val Int.megajoules: Energy get() = toEnergy(EnergyUnit.International.Megajoule)
        public inline val Long.megajoules: Energy get() = toEnergy(EnergyUnit.International.Megajoule)
        public inline val Int.gigajoules: Energy get() = toEnergy(EnergyUnit.International.Gigajoule)
        public inline val Long.gigajoules: Energy get() = toEnergy(EnergyUnit.International.Gigajoule)
        public inline val Int.tetrajoules: Energy get() = toEnergy(EnergyUnit.International.Tetrajoule)
        public inline val Long.tetrajoules: Energy get() = toEnergy(EnergyUnit.International.Tetrajoule)
        public inline val Int.petajoules: Energy get() = toEnergy(EnergyUnit.International.Petajoule)
        public inline val Long.petajoules: Energy get() = toEnergy(EnergyUnit.International.Petajoule)

        public inline val Int.milliwattHours: Energy get() = toEnergy(EnergyUnit.Electricity.MilliwattHour)
        public inline val Long.milliwattHours: Energy get() = toEnergy(EnergyUnit.Electricity.MilliwattHour)
        public inline val Int.wattHours: Energy get() = toEnergy(EnergyUnit.Electricity.WattHour)
        public inline val Long.wattHours: Energy get() = toEnergy(EnergyUnit.Electricity.WattHour)
        public inline val Int.kilowattHours: Energy get() = toEnergy(EnergyUnit.Electricity.KilowattHour)
        public inline val Long.kilowattHours: Energy get() = toEnergy(EnergyUnit.Electricity.KilowattHour)
        public inline val Int.megawattHours: Energy get() = toEnergy(EnergyUnit.Electricity.MegawattHour)
        public inline val Long.megawattHours: Energy get() = toEnergy(EnergyUnit.Electricity.MegawattHour)
        public inline val Int.gigawattHours: Energy get() = toEnergy(EnergyUnit.Electricity.GigawattHour)
        public inline val Long.gigawattHours: Energy get() = toEnergy(EnergyUnit.Electricity.GigawattHour)
        public inline val Int.terawattHours: Energy get() = toEnergy(EnergyUnit.Electricity.TerawattHour)
        public inline val Long.terawattHours: Energy get() = toEnergy(EnergyUnit.Electricity.TerawattHour)

        internal inline val OverflowLong.millijoules get() = rawValue.toEnergy(EnergyUnit.International.Millijoule)

        public val POSITIVE_INFINITY: Energy = Energy(OverflowLong.POSITIVE_INFINITY)
        public val NEGATIVE_INFINITY: Energy = Energy(OverflowLong.NEGATIVE_INFINITY)
    }
}

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

public fun Int.toEnergy(unit: EnergyUnit): Energy {
   return toLong().toEnergy(unit)
}

public fun Long.toEnergy(unit: EnergyUnit): Energy {
    return Energy(this.noOverflow * unit.millijouleScale)
}
