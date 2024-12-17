package io.github.kevincianfarini.alchemist

import io.github.kevincianfarini.alchemist.OverflowLong.Companion.noOverflow
import kotlin.jvm.JvmInline
import kotlin.text.Typography.nbsp

/**
 * Represents a temperature and is capable of storing ±9.2 billion Kelvin (±9.2 billion °C, ±16.6 billion °F) at
 * nanokelvin precision.
 */
@JvmInline
public value class Temperature internal constructor(private val rawNanokelvin: OverflowLong) : Comparable<Temperature> {

    /**
     * Returns the number that is the ratio of this and the [other] temperature value.
     */
    public operator fun div(other: Temperature): Double {
        return rawNanokelvin.toDouble() / other.rawNanokelvin.toDouble()
    }

    /**
     * Returns a temperature whose value is this temperature value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Temperature = div(scale.toLong())

    /**
     * Returns a temperature whose value is this temperature value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Temperature = Temperature(rawNanokelvin / scale)

    /**
     * Returns a temperature whose value is the difference between this and the [other] temperature value.
     */
    public operator fun minus(other: Temperature): Temperature = Temperature(rawNanokelvin - other.rawNanokelvin)

    /**
     * Returns a temperature whose value is the sum between this and the [other] temperature value.
     */
    public operator fun plus(other: Temperature): Temperature = Temperature(rawNanokelvin + other.rawNanokelvin)

    /**
     * Returns a temperature whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Temperature = times(scale.toLong())

    /**
     * Returns a temperature whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Temperature = Temperature(rawNanokelvin * scale)

    public fun toString(unit: TemperatureUnit): String {
        return "${unit.convertNanokelvinsToThis(rawNanokelvin)}${unit.symbol}"
    }

    public override fun toString(): String {
        return toString(toStringUnit())
    }

    public override fun compareTo(other: Temperature): Int {
        return rawNanokelvin.compareTo(other.rawNanokelvin)
    }

    private fun toStringUnit(): TemperatureUnit {
        if (TemperatureUnit.International.Gigakelvin.convertNanokelvinsToThis(rawNanokelvin) >= 1.0) {
            return TemperatureUnit.International.Gigakelvin
        }
        if (TemperatureUnit.International.Megakelvin.convertNanokelvinsToThis(rawNanokelvin) >= 1.0) {
            return TemperatureUnit.International.Megakelvin
        }
        if (TemperatureUnit.International.Kilokelvin.convertNanokelvinsToThis(rawNanokelvin) >= 1.0) {
            return TemperatureUnit.International.Kilokelvin
        }
        if (TemperatureUnit.International.Kelvin.convertNanokelvinsToThis(rawNanokelvin) >= 1.0) {
            return TemperatureUnit.International.Kelvin
        }
        if (TemperatureUnit.International.Millikelvin.convertNanokelvinsToThis(rawNanokelvin) >= 1.0) {
            return TemperatureUnit.International.Millikelvin
        }
        if (TemperatureUnit.International.Microkelvin.convertNanokelvinsToThis(rawNanokelvin) >= 1.0) {
            return TemperatureUnit.International.Microkelvin
        }
        return TemperatureUnit.International.Nanokelvin
    }

    public companion object {

        public val Int.nanokelvins: Temperature get() = toLong().nanokelvins
        public val Long.nanokelvins: Temperature get() = noOverflow.nanokelvins
        private inline val OverflowLong.nanokelvins get() = Temperature(
            TemperatureUnit.International.Nanokelvin.convertToNanokelvin(this)
        )

        public val Int.microkelvins: Temperature get() = toLong().microkelvins
        public val Long.microkelvins: Temperature get() = noOverflow.microkelvins
        private inline val OverflowLong.microkelvins get() = Temperature(
            TemperatureUnit.International.Microkelvin.convertToNanokelvin(this)
        )

        public val Int.millikelvins: Temperature get() = toLong().millikelvins
        public val Long.millikelvins: Temperature get() = noOverflow.millikelvins
        private inline val OverflowLong.millikelvins get() = Temperature(
            TemperatureUnit.International.Millikelvin.convertToNanokelvin(this)
        )

        public val Int.kelvins: Temperature get() = toLong().kelvins
        public val Long.kelvins: Temperature get() = noOverflow.kelvins
        private inline val OverflowLong.kelvins get() = Temperature(
            TemperatureUnit.International.Kelvin.convertToNanokelvin(this)
        )

        public val Int.kilokelvins: Temperature get() = toLong().kilokelvins
        public val Long.kilokelvins: Temperature get() = noOverflow.kilokelvins
        private inline val OverflowLong.kilokelvins get() = Temperature(
            TemperatureUnit.International.Kilokelvin.convertToNanokelvin(this)
        )

        public val Int.megakelvins: Temperature get() = toLong().megakelvins
        public val Long.megakelvins: Temperature get() = noOverflow.megakelvins
        private inline val OverflowLong.megakelvins get() = Temperature(
            TemperatureUnit.International.Megakelvin.convertToNanokelvin(this)
        )

        public val Int.gigskelvins: Temperature get() = toLong().gigakelvins
        public val Long.gigakelvins: Temperature get() = noOverflow.gigakelvins
        private inline val OverflowLong.gigakelvins get() = Temperature(
            TemperatureUnit.International.Gigakelvin.convertToNanokelvin(this)
        )

        public val Int.celsius: Temperature get() = toLong().celsius
        public val Long.celsius: Temperature get() = noOverflow.celsius
        private inline val OverflowLong.celsius get() = Temperature(
            TemperatureUnit.International.Celsius.convertToNanokelvin(this)
        )

        public val Int.fahrenheit: Temperature get() = toLong().fahrenheit
        public val Long.fahrenheit: Temperature get() = noOverflow.fahrenheit
        private inline val OverflowLong.fahrenheit get() = Temperature(
            TemperatureUnit.Fahrenheit.convertToNanokelvin(this)
        )
    }
}

@RequiresOptIn(
    message = """
        TemperatureUnit exposes OverflowLong, an integer type which might produce unexpected arithmetic results. 
        Implementors should use caution when converting between their custom temperature units and nanokelvins. 
    """,
    level = RequiresOptIn.Level.ERROR,
)
public annotation class DelicateTemperatureUnit()

/**
 * A unit of temperature precise to the nanokelvin.
 */
@SubclassOptInRequired(DelicateTemperatureUnit::class)
public interface TemperatureUnit {

    /**
     * The symbol of this unit.
     */
    public val symbol: String

    /**
     * Convert the degrees of this [TemperatureUnit] to nanokelvins.
     */
    public fun convertToNanokelvin(degrees: OverflowLong): OverflowLong

    /**
     * Convert the amount of [nanokelvins] of this degrees in this [TemperatureUnit].
     */
    public fun convertNanokelvinsToThis(nanokelvins: OverflowLong): Double

    public enum class International(override val symbol: String) : TemperatureUnit {
        Nanokelvin("${nbsp}nK") {
            override fun convertToNanokelvin(degrees: OverflowLong): OverflowLong = degrees
            override fun convertNanokelvinsToThis(nanokelvins: OverflowLong): Double = nanokelvins.toDouble()
        },
        Microkelvin("${nbsp}μK") {
            override fun convertToNanokelvin(degrees: OverflowLong): OverflowLong = degrees * 1_000
            override fun convertNanokelvinsToThis(nanokelvins: OverflowLong): Double = nanokelvins.toDouble() / 1_000
        },
        Millikelvin("${nbsp}mK") {
            override fun convertToNanokelvin(degrees: OverflowLong): OverflowLong = degrees * 1_000_000
            override fun convertNanokelvinsToThis(nanokelvins: OverflowLong): Double = nanokelvins.toDouble() / 1_000_000
        },
        Kelvin("${nbsp}K") {
            override fun convertToNanokelvin(degrees: OverflowLong): OverflowLong = degrees * 1_000_000_000
            override fun convertNanokelvinsToThis(nanokelvins: OverflowLong): Double = nanokelvins.toDouble() / 1_000_000_000
        },
        Kilokelvin("${nbsp}kK") {
            override fun convertToNanokelvin(degrees: OverflowLong): OverflowLong = degrees * 1_000_000_000_000
            override fun convertNanokelvinsToThis(nanokelvins: OverflowLong): Double = nanokelvins.toDouble() / 1_000_000_000_000
        },
        Megakelvin("${nbsp}MK") {
            override fun convertToNanokelvin(degrees: OverflowLong): OverflowLong = degrees * 1_000_000_000_000_000
            override fun convertNanokelvinsToThis(nanokelvins: OverflowLong): Double = nanokelvins.toDouble() / 1_000_000_000_000_000
        },
        Gigakelvin("${nbsp}GK") {
            override fun convertToNanokelvin(degrees: OverflowLong): OverflowLong = degrees * 1_000_000_000_000_000_000
            override fun convertNanokelvinsToThis(nanokelvins: OverflowLong): Double = nanokelvins.toDouble() / 1_000_000_000_000_000_000
        },
        Celsius("°C") {
            override fun convertToNanokelvin(degrees: OverflowLong): OverflowLong {
                return (degrees * 1_000_000_000) + 273_150_000_000
            }
            override fun convertNanokelvinsToThis(nanokelvins: OverflowLong): Double {
                return (nanokelvins.toDouble() - 273_150_000_000) / 1_000_000_000
            }
        },
    }

    public companion object {
        public val Fahrenheit: TemperatureUnit = object : TemperatureUnit {
            override val symbol: String get() = "°F"
            override fun convertToNanokelvin(degrees: OverflowLong): OverflowLong {
                val accurate = (((degrees * 1_000_000_000) + 459_670_000_000) * 5) / 9
                return if (accurate.isFinite()) {
                    accurate
                } else {
                    (degrees * 555_555_556) + 255_372_222_222
                }
            }
            override fun convertNanokelvinsToThis(nanokelvins: OverflowLong): Double {
                return (nanokelvins - 255_372_222_222).toDouble() / 555_555_556.toDouble()
            }
        }
    }
}