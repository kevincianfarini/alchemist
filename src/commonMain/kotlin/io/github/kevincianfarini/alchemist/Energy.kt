package io.github.kevincianfarini.alchemist

import io.github.kevincianfarini.alchemist.OverflowLong.Companion.noOverflow
import kotlin.jvm.JvmInline
import kotlin.time.Duration

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
     * @throws IllegalArgumentException if both this energy and [duration] are infinite.
     */
    public operator fun div(duration: Duration): Power = when {
        rawMillijoules.isInfinite() && duration.isInfinite() -> {
            throw IllegalArgumentException("Dividing two infinite values yields an undefined result.")
        }
        rawMillijoules.isInfinite() -> Power(rawMillijoules)
        duration.isInfinite() -> Power(0L.noOverflow)
        duration.inWholeMicroseconds != Long.MAX_VALUE && duration.inWholeMicroseconds != Long.MIN_VALUE -> {
            // Try to convert to smaller joule subdivisions to retain precision.
            // 1 picojoule per 1 microsecond is 1 microwatt.
            // 1 nanojoule per 1 microsecond is 1,000 microwatts.
            // 1 microjoule per 1 microsecond is 1,000,000 microwatts.
            // 1 millijoule per 1 microsecond is 1,000,000,000 microwatts.
            val microjoules = rawMillijoules * 1_000
            val nanojoules = microjoules * 1_000
            val picojoules = nanojoules * 1_000
            when  {
                picojoules.isFinite() -> Power(picojoules / duration.inWholeMicroseconds)
                nanojoules.isFinite() -> Power((nanojoules / duration.inWholeMicroseconds) * 1_000)
                microjoules.isFinite() -> Power((microjoules / duration.inWholeMicroseconds) * 1_000_000)
                else -> Power((rawMillijoules / duration.inWholeMicroseconds) * 1_000_000_000)
            }
        }
        else -> {
            // Try to convert to smaller joule subdivisions to retain precision.
            // 1 nanojoule per 1 millisecond is 1 microwatts.
            // 1 microjoule per 1 millisecond is 1,000 microwatts.
            // 1 millijoule per 1 millisecond is 1,000,000 microwatts.
            val microjoules = rawMillijoules * 1_000
            val nanojoules = microjoules * 1_000
            when  {
                nanojoules.isFinite() -> Power((nanojoules / duration.inWholeMilliseconds))
                microjoules.isFinite() -> Power((microjoules / duration.inWholeMilliseconds) * 1_000)
                else -> Power((rawMillijoules / duration.inWholeMilliseconds) * 1_000_000)
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

        public val POSITIVE_INFINITY: Energy = Energy(OverflowLong.POSITIVE_INFINITY)
        public val NEGATIVE_INFINITY: Energy = Energy(OverflowLong.NEGATIVE_INFINITY)
    }
}

public sealed interface EnergyUnit {

    public enum class International(internal val millijouleScale: Long, internal val symbol: String) : EnergyUnit {
        Millijoule(1, "mJ"),
        Joule(1_000, "J"),
        Kilojoule(1_000_000, "kJ"),
        Megajoule(1_000_000_000, "MJ"),
        Gigajoule(1_000_000_000_000, "GJ"),
        Tetrajoule(1_000_000_000_000_000, "TJ"),
        Petajoule(1_000_000_000_000_000_000, "PJ"),
    }

    public enum class Electricity(internal val millijouleScale: Long, internal val symbol: String) : EnergyUnit {
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

private val EnergyUnit.millijouleScale: Long get() = when (this) {
    is EnergyUnit.Electricity -> millijouleScale
    is EnergyUnit.International -> millijouleScale
}

private val EnergyUnit.symbol: String get() = when (this) {
    is EnergyUnit.Electricity -> symbol
    is EnergyUnit.International -> symbol
}
