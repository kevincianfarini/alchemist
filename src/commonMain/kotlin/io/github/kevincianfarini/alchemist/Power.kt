package io.github.kevincianfarini.alchemist

import io.github.kevincianfarini.alchemist.OverflowLong.Companion.noOverflow
import kotlin.jvm.JvmInline
import kotlin.time.Duration

@JvmInline
public value class Power internal constructor(private val rawMicrowatts: OverflowLong) {

    /**
     * Returns the number that is the ratio of this and the [other] power value.
     */
    public operator fun div(other: Power): Double {
        return rawMicrowatts.toDouble() / other.rawMicrowatts.toDouble()
    }

    /**
     * Returns a power whose value is this power value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Power = TODO()

    /**
     * Returns a power whose value is this power value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Power = TODO()

    /**
     * Returns a power whose value is the difference between this and the [other] power value.
     */
    public operator fun minus(other: Power): Power = TODO()

    /**
     * Returns a power whose value is the sum between this and the [other] power value.
     */
    public operator fun plus(other: Power): Power = TODO()

    /**
     * Returns the resulting [Energy] from applying this power over the specified [duration].
     */
    public operator fun times(duration: Duration): Energy = TODO()

    /**
     * Returns a power whose value is this power multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Power = TODO()

    /**
     * Returns a power whose value is this power multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Power = TODO()

    public fun toDouble(unit: PowerUnit): Double {
        return this / unit.microwattScale.microwatts
    }

    public fun toString(unit: PowerUnit): String = when (rawMicrowatts.isInfinite()) {
        true -> rawMicrowatts.toString()
        false -> "${toDouble(unit)}${unit.symbol}"
    }

    override fun toString(): String {
        val largestUnit = PowerUnit.entries.asReversed().first { unit ->
            rawMicrowatts.absoluteValue / unit.microwattScale > 0
        }
        return toString(largestUnit)
    }

    public companion object {
        public inline val Int.watts: Power get() = toPower(PowerUnit.Watt)
        public inline val Long.watts: Power get() = toPower(PowerUnit.Watt)

        public inline val Int.microwatts: Power get() = toPower(PowerUnit.Microwatt)
        public inline val Long.microwatts: Power get() = toPower(PowerUnit.Microwatt)

        public val POSITIVE_INFINITY: Power = Power(OverflowLong.POSITIVE_INFINITY)
        public val NEGATIVE_INFINITY: Power = Power(OverflowLong.NEGATIVE_INFINITY)
    }
}

public fun Int.toPower(unit: PowerUnit): Power {
    return toLong().toPower(unit)
}

public fun Long.toPower(unit: PowerUnit): Power {
    return Power(this.noOverflow * unit.microwattScale)
}

public enum class PowerUnit(internal val microwattScale: Long, internal val symbol: String) {
    Microwatt(1, "μW"),
    Milliwatt(1_000, "mW"),
    Watt(1_000_000, "W"),
    Kilowatt(1_000_000_000, "kW"),
    Megawatt(1_000_000_000_000, "MW"),
    Gigawatt(1_000_000_000_000_000, "GW"),
    Terawatt(1_000_000_000_000_000_000, "TW"),
}