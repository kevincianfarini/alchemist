package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline
import kotlin.time.Duration

/**
 * Represents a measure of distance and is capable of storing ±9.2 million kilometers at nanometer precision.
 */
@JvmInline
public value class Distance internal constructor(private val rawNanometers: Long) {

    /**
     * Returns the constant [Velocity] required to travel this distance in the specified [duration].
     */
    public operator fun div(duration: Duration): Velocity = TODO()

    /**
     * Returns the number that is the ratio of this and the [other] distance value.
     */
    public operator fun div(other: Distance): Double {
        return rawNanometers.toDouble() / other.rawNanometers.toDouble()
    }

    /**
     * Returns a distance whose value is this distance value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Distance {
        return Distance(rawNanometers / scale)
    }

    /**
     * Returns a distance whose value is this distance value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Distance {
        return Distance(rawNanometers / scale)
    }

    /**
     * Returns a distance whose value is the difference between this and the [other] distance value.
     */
    public operator fun minus(other: Distance): Distance {
        return Distance(rawNanometers - other.rawNanometers)
    }

    /**
     * Returns a distance whose value is the sum between this and the [other] distance value.
     */
    public operator fun plus(other: Distance): Distance {
        return Distance(rawNanometers + other.rawNanometers)
    }

    /**
     * Returns the resulting [Area] after multiplying this distance by the [other] distance value.
     */
    public operator fun times(other: Distance): Area = TODO()

    /**
     * Returns a distance whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Distance {
        return Distance(rawNanometers * scale)
    }

    /**
     * Returns a distance whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Distance {
        return Distance(rawNanometers * scale)
    }

    public fun toDouble(unit: DistanceUnit): Double {
        return this / unit.nanometerScale.nanometers
    }

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
    ): T {
        val giga = rawNanometers / DistanceUnit.International.Gigameter.nanometerScale
        val gigaRemainder = rawNanometers % DistanceUnit.International.Gigameter.nanometerScale
        val mega = gigaRemainder / DistanceUnit.International.Megameter.nanometerScale
        val megaRemainder = gigaRemainder % DistanceUnit.International.Megameter.nanometerScale
        val kilo = megaRemainder / DistanceUnit.International.Kilometer.nanometerScale
        val kiloRemainder = megaRemainder % DistanceUnit.International.Kilometer.nanometerScale
        val meters = kiloRemainder / DistanceUnit.International.Meter.nanometerScale
        val metersRemainder = kiloRemainder % DistanceUnit.International.Meter.nanometerScale
        val centi = metersRemainder / DistanceUnit.International.Centimeter.nanometerScale
        val centiRemainder = metersRemainder % DistanceUnit.International.Centimeter.nanometerScale
        val milli = centiRemainder / DistanceUnit.International.Millimeter.nanometerScale
        val milliRemainder = centiRemainder % DistanceUnit.International.Millimeter.nanometerScale
        val micro = milliRemainder / DistanceUnit.International.Micrometer.nanometerScale
        val nano = milliRemainder % DistanceUnit.International.Micrometer.nanometerScale
        return action(giga, mega, kilo, meters, centi, milli, micro, nano)
    }

    public fun <T> toUnitedStatesCustomaryComponents(
        action: (miles: Long, yards: Long, feet: Long, inches: Double) -> T,
    ): T {
        val miles = rawNanometers / DistanceUnit.UnitedStatesCustomary.Mile.nanometerScale
        val milesRemainder = rawNanometers % DistanceUnit.UnitedStatesCustomary.Mile.nanometerScale
        val yards = milesRemainder / DistanceUnit.UnitedStatesCustomary.Yard.nanometerScale
        val yardRemainder = milesRemainder % DistanceUnit.UnitedStatesCustomary.Yard.nanometerScale
        val feet = yardRemainder / DistanceUnit.UnitedStatesCustomary.Foot.nanometerScale
        val feetRemainder = yardRemainder % DistanceUnit.UnitedStatesCustomary.Foot.nanometerScale
        val inches = feetRemainder.nanometers.toDouble(DistanceUnit.UnitedStatesCustomary.Inch)
        return action(miles, yards, feet, inches)
    }

    /**
     * Returns a fractional string representation of this distance expressed in the specified [unit].
     */
    public fun toString(unit: DistanceUnit): String {
        return "${toDouble(unit)}${unit.symbol}"
    }

    /**
     * Returns a string representation of this distance expressed in its [metric][DistanceUnit.International] components.
     */
    public override fun toString(): String {
        val largestUnit = DistanceUnit.International.entries.asReversed().first { unit ->
            rawNanometers / unit.nanometerScale > 0
        }
        return toString(largestUnit)
    }

    public companion object {

        /**
         * Returns a [Distance] equal to [Int] number of nanometers.
         */
        public inline val Int.nanometers: Distance get() = toDistance(DistanceUnit.International.Nanometer)

        /**
         * Returns a [Distance] equal to [Long] number of nanometers.
         */
        public inline val Long.nanometers: Distance get() = toDistance(DistanceUnit.International.Nanometer)

        /**
         * Returns a [Distance] equal to [Int] number of micrometers.
         */
        public inline val Int.micrometers: Distance get() = toDistance(DistanceUnit.International.Micrometer)

        /**
         * Returns a [Distance] equal to [Long] number of micrometers.
         */
        public inline val Long.micrometers: Distance get() = toDistance(DistanceUnit.International.Micrometer)

        /**
         * Returns a [Distance] equal to [Int] number of millimeters.
         */
        public inline val Int.millimeters: Distance get() = toDistance(DistanceUnit.International.Millimeter)

        /**
         * Returns a [Distance] equal to [Long] number of millimeters.
         */
        public inline val Long.millimeters: Distance get() = toDistance(DistanceUnit.International.Millimeter)

        /**
         * Returns a [Distance] equal to [Int] number of centimeters.
         */
        public inline val Int.centimeters: Distance get() = toDistance(DistanceUnit.International.Centimeter)

        /**
         * Returns a [Distance] equal to [Long] number of centimeters.
         */
        public inline val Long.centimeters: Distance get() = toDistance(DistanceUnit.International.Centimeter)

        /**
         * Returns a [Distance] equal to [Int] number of meters.
         */
        public inline val Int.meters: Distance get() = toDistance(DistanceUnit.International.Meter)

        /**
         * Returns a [Distance] equal to [Long] number of meters.
         */
        public inline val Long.meters: Distance get() = toDistance(DistanceUnit.International.Meter)

        /**
         * Returns a [Distance] equal to [Int] number of kilometers.
         */
        public inline val Int.kilometers: Distance get() = toDistance(DistanceUnit.International.Kilometer)

        /**
         * Returns a [Distance] equal to [Long] number of kilometers.
         */
        public inline val Long.kilometers: Distance get() = toDistance(DistanceUnit.International.Kilometer)

        /**
         * Returns a [Distance] equal to [Int] number of megameters.
         */
        public inline val Int.megameters: Distance get() = toDistance(DistanceUnit.International.Megameter)

        /**
         * Returns a [Distance] equal to [Long] number of megameters.
         */
        public inline val Long.megameters: Distance get() = toDistance(DistanceUnit.International.Megameter)

        /**
         * Returns a [Distance] equal to [Int] number of gigameters.
         */
        public inline val Int.gigameters: Distance get() = toDistance(DistanceUnit.International.Gigameter)

        /**
         * Returns a [Distance] equal to [Long] number of gigameters.
         */
        public inline val Long.gigameters: Distance get() = toDistance(DistanceUnit.International.Gigameter)

        /**
         * Returns a [Distance] equal to [Int] number of inches.
         */
        public inline val Int.inches: Distance get() = toDistance(DistanceUnit.UnitedStatesCustomary.Inch)

        /**
         * Returns a [Distance] equal to [Long] number of inches.
         */
        public inline val Long.inches: Distance get() = toDistance(DistanceUnit.UnitedStatesCustomary.Inch)

        /**
         * Returns a [Distance] equal to [Int] number of feet.
         */
        public inline val Int.feet: Distance get() = toDistance(DistanceUnit.UnitedStatesCustomary.Foot)

        /**
         * Returns a [Distance] equal to [Long] number of feet.
         */
        public inline val Long.feet: Distance get() = toDistance(DistanceUnit.UnitedStatesCustomary.Foot)

        /**
         * Returns a [Distance] equal to [Int] number of yards.
         */
        public inline val Int.yards: Distance get() = toDistance(DistanceUnit.UnitedStatesCustomary.Yard)

        /**
         * Returns a [Distance] equal to [Long] number of yards.
         */
        public inline val Long.yards: Distance get() = toDistance(DistanceUnit.UnitedStatesCustomary.Yard)

        /**
         * Returns a [Distance] equal to [Int] number of miles.
         */
        public inline val Int.miles: Distance get() = toDistance(DistanceUnit.UnitedStatesCustomary.Mile)

        /**
         * Returns a [Distance] equal to [Long] number of miles.
         */
        public inline val Long.miles: Distance get() = toDistance(DistanceUnit.UnitedStatesCustomary.Mile)
    }
}

public fun Int.toDistance(unit: DistanceUnit): Distance {
    return toLong().toDistance(unit)
}

public fun Long.toDistance(unit: DistanceUnit): Distance {
    return Distance(this * unit.nanometerScale)
}


public sealed interface DistanceUnit {

    public enum class International(internal val nanometerScale: Long, internal val symbol: String) : DistanceUnit {
        Nanometer(1, "nm"),
        Micrometer(1_000, "μm"),
        Millimeter(1_000_000, "mm"),
        Centimeter(10_000_000, "cm"),
        Meter(1_000_000_000, "m"),
        Kilometer(1_000_000_000_000, "km"),
        Megameter(1_000_000_000_000_000, "Mm"),
        Gigameter(1_000_000_000_000_000_000, "Gm"),
    }

    public enum class UnitedStatesCustomary(internal val nanometerScale: Long, internal val symbol: String) : DistanceUnit {
        Inch(25_400_000, "in"),
        Foot(304_800_000, "ft"),
        Yard(914_400_000, "yd"),
        Mile(1_609_344_000_000, "mi"),
    }
}

private val DistanceUnit.nanometerScale: Long get() = when (this) {
    is DistanceUnit.UnitedStatesCustomary -> nanometerScale
    is DistanceUnit.International -> nanometerScale
}

private val DistanceUnit.symbol: String get() = when (this) {
    is DistanceUnit.UnitedStatesCustomary -> symbol
    is DistanceUnit.International -> symbol
}