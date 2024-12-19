package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline
import kotlin.time.Duration
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Represents an amount of power and is capable of storing ±9.22 terawatts at microwatt precision.
 */
@JvmInline
public value class Power internal constructor(private val rawMicrowatts: SaturatingLong) : Comparable<Power> {

    /**
     * Returns the number that is the ratio of this and the [other] power value.
     */
    public operator fun div(other: Power): Double {
        return rawMicrowatts.toDouble() / other.rawMicrowatts.toDouble()
    }

    /**
     * Returns a power whose value is this power value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Power = div(scale.toLong())

    /**
     * Returns a power whose value is this power value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Power = Power(rawMicrowatts / scale)

    /**
     * Returns a power whose value is the difference between this and the [other] power value.
     */
    public operator fun minus(other: Power): Power = Power(rawMicrowatts - other.rawMicrowatts)

    /**
     * Returns a power whose value is the sum between this and the [other] power value.
     */
    public operator fun plus(other: Power): Power = Power(rawMicrowatts + other.rawMicrowatts)

    /**
     * Returns the resulting [Energy] from applying this power over the specified [duration].
     *
     * This operation attempts to retain precision, but for sufficiently large values of either this power or [duration],
     * some precision may be lost.
     *
     * @throws IllegalArgumentException if this power is infinite and duration is zero, or if this power is zero and
     * duration is infinite.
     */
    public operator fun times(duration: Duration): Energy {
        return when {
            duration.isInfinite() || rawMicrowatts.isInfinite() -> {
               Energy(SaturatingLong.POSITIVE_INFINITY * duration.sign * rawMicrowatts)
            }
            else -> duration.toEnergyComponents { thousandSeconds, secondsRemainder, millis, micros, nanos ->
                // Try to find the right level which we can perform this operation at without losing precision.
                // --------------------------------------------------------------------------------------------
                // 1 microwatt * 1 nanosecond is 1 femtojoule.
                // 1 microwatt * 1 microsecond is 1 picojoule.
                // 1 microwatt * 1 millisecond is 1 nanojoule.
                // 1 microwatt * 1 second is 1 microjoule.
                // 1 microwatt * 1,000 seconds is 1 millijoule.
                // --------------------------------------------------------------------------------------------
                val millijoules = rawMicrowatts * thousandSeconds
                val microjoules = rawMicrowatts * secondsRemainder
                val nanojoules = rawMicrowatts * millis
                val picojoules = rawMicrowatts * micros
                val femtojoules = rawMicrowatts * nanos
                // ----------- Try femtojoule precision. ------------------------------------------------------
                val femtoJ = femtojoules + (picojoules * 1_000) + (nanojoules * 1_000_000) + (microjoules * 1_000_000_000) + (millijoules * 1_000_000_000_000)
                if (femtoJ.isFinite()) return@toEnergyComponents Energy(femtoJ / 1_000_000_000_000)
                // ----------- Try picojoule precision. -------------------------------------------------------
                val picoJ = (femtojoules / 1_000) + picojoules + (nanojoules * 1_000) + (microjoules * 1_000_000) + (millijoules * 1_000_000_000)
                if (picoJ.isFinite()) return@toEnergyComponents Energy(picoJ / 1_000_000_000)
                // ----------- Try nanojoule precision. -------------------------------------------------------
                val nanoJ = (femtojoules / 1_000_000) + (picojoules / 1_000) + nanojoules + (microjoules * 1_000) + (millijoules * 1_000_000)
                if (nanoJ.isFinite()) return@toEnergyComponents Energy(nanoJ / 1_000_000)
                // ----------- Try microjoule precision. -------------------------------------------------------
                val microJ = (femtojoules / 1_000_000_000) + (picojoules / 1_000_000) + (nanojoules / 1_000) + microjoules + (millijoules * 1_000)
                if (microJ.isFinite()) return@toEnergyComponents Energy(microJ / 1_000)
                // ----------- Default microjoule precision. ---------------------------------------------------
                val milliJ = (femtojoules / 1_000_000_000_000) + (picojoules / 1_000_000_000) + (nanojoules / 1_000_000) + (microjoules / 1_000) + millijoules
                Energy(milliJ)
            }
        }
    }

    /**
     * Returns a power whose value is this power multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Power = times(scale.toLong())

    /**
     * Returns a power whose value is this power multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Power = Power(rawMicrowatts * scale)

    public fun toDouble(unit: PowerUnit): Double {
        return this / unit.microwattScale.microwatts
    }

    public fun toString(unit: PowerUnit): String = when (rawMicrowatts.isInfinite()) {
        true -> rawMicrowatts.toString()
        false -> "${toDouble(unit)}${unit.symbol}"
    }

    override fun toString(): String {
        val largestUnit = PowerUnit.International.entries.asReversed().first { unit ->
            rawMicrowatts.absoluteValue / unit.microwattScale > 0
        }
        return toString(largestUnit)
    }

    public override fun compareTo(other: Power): Int {
        return rawMicrowatts.compareTo(other.rawMicrowatts)
    }

    public companion object {
        public val POSITIVE_INFINITY: Power = Power(SaturatingLong.POSITIVE_INFINITY)
        public val NEGATIVE_INFINITY: Power = Power(SaturatingLong.NEGATIVE_INFINITY)
    }
}

public inline val Int.terawatts: Power get() = toPower(PowerUnit.International.Terawatt)
public inline val Long.terawatts: Power get() = toPower(PowerUnit.International.Terawatt)

public inline val Int.gigawatts: Power get() = toPower(PowerUnit.International.Gigawatt)
public inline val Long.gigawatts: Power get() = toPower(PowerUnit.International.Gigawatt)

public inline val Int.megawatts: Power get() = toPower(PowerUnit.International.Megawatt)
public inline val Long.megawatts: Power get() = toPower(PowerUnit.International.Megawatt)

public inline val Int.kilowatts: Power get() = toPower(PowerUnit.International.Kilowatt)
public inline val Long.kilowatts: Power get() = toPower(PowerUnit.International.Kilowatt)

public inline val Int.watts: Power get() = toPower(PowerUnit.International.Watt)
public inline val Long.watts: Power get() = toPower(PowerUnit.International.Watt)

public inline val Int.milliwatts: Power get() = toPower(PowerUnit.International.Milliwatt)
public inline val Long.milliwatts: Power get() = toPower(PowerUnit.International.Milliwatt)

public inline val Int.microwatts: Power get() = toPower(PowerUnit.International.Microwatt)
public inline val Long.microwatts: Power get() = toPower(PowerUnit.International.Microwatt)

internal inline val SaturatingLong.megawatts get() = rawValue.toPower(PowerUnit.International.Megawatt)
internal inline val SaturatingLong.kilowatts get() = rawValue.toPower(PowerUnit.International.Kilowatt)
internal inline val SaturatingLong.watts get() = rawValue.toPower(PowerUnit.International.Watt)
internal inline val SaturatingLong.milliwatts get() = rawValue.toPower(PowerUnit.International.Milliwatt)
internal inline val SaturatingLong.microwatts get() = rawValue.toPower(PowerUnit.International.Microwatt)

public fun Int.toPower(unit: PowerUnit): Power {
    return toLong().toPower(unit)
}

public fun Long.toPower(unit: PowerUnit): Power {
    return Power(this.saturated * unit.microwattScale)
}

/**
 * A unit of distance precise to the microwatt.
 */
public interface PowerUnit {

    /**
     * The amount of microwatts in this unit. Implementations of [PowerUnit] should be perfectly divisible by a
     * quantity of microwatts.
     */
    public val microwattScale: Long

    /**
     * The symbol of this unit.
     */
    public val symbol: String

    public enum class International(
        override val microwattScale: Long,
        override val symbol: String,
    ) : PowerUnit {
        Microwatt(1, "μW"),
        Milliwatt(1_000, "mW"),
        Watt(1_000_000, "W"),
        Kilowatt(1_000_000_000, "kW"),
        Megawatt(1_000_000_000_000, "MW"),
        Gigawatt(1_000_000_000_000_000, "GW"),
        Terawatt(1_000_000_000_000_000_000, "TW"),
    }
}

private fun Duration.toEnergyComponents(
    action: (
        thousandSeconds: Long,
        seconds: Long,
        millis: Long,
        micros: Long,
        nanos: Long
    ) -> Energy,
): Energy {
    val seconds = inWholeSeconds
    val secondsRemainder = this - seconds.seconds
    val millis = secondsRemainder.inWholeMilliseconds
    val millisRemainder = secondsRemainder - millis.milliseconds
    val micros = millisRemainder.inWholeMicroseconds
    val nanos = (millisRemainder - micros.microseconds).inWholeNanoseconds
    return action(seconds / 1_000, seconds % 1_000, millis, micros, nanos)
}

private val Duration.sign: Int get() = when {
    isPositive() -> 1
    isNegative() -> -1
    else -> 0
}
