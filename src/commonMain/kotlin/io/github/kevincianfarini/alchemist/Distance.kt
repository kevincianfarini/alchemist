package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline
import kotlin.time.Duration

/**
 * Represents a measure of distance and is capable of storing ±9.2 million kilometers.
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
    public operator fun div(other: Distance): Double = TODO()

    /**
     * Returns a distance whose value is this distance value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Distance = TODO()

    /**
     * Returns a distance whose value is this distance value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Distance = TODO()

    /**
     * Returns a distance whose value is the difference between this and the [other] distance value.
     */
    public operator fun minus(other: Distance): Distance = TODO()

    /**
     * Returns a distance whose value is the sum between this and the [other] distance value.
     */
    public operator fun plus(other: Distance): Distance = TODO()

    /**
     * Returns the resulting [Area] after multiplying this distance by the [other] distance value.
     */
    public operator fun times(other: Distance): Area = TODO()

    /**
     * Returns a distance whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Distance = TODO()

    /**
     * Returns a distance whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Distance = TODO()

    public companion object {

        /**
         * Returns a [Distance] equal to [Int] number of nanometers.
         */
        public inline val Int.nanometers: Distance get() = TODO()

        /**
         * Returns a [Distance] equal to [Int] number of micrometers.
         */
        public inline val Int.micrometers: Distance get() = TODO()

        /**
         * Returns a [Distance] equal to [Int] number of millimeters.
         */
        public inline val Int.millimeter: Distance get() = TODO()

        /**
         * Returns a [Distance] equal to [Int] number of centimeters.
         */
        public inline val Int.centimeter: Distance get() = TODO()

        /**
         * Returns a [Distance] equal to [Int] number of meters.
         */
        public inline val Int.meters: Distance get() = TODO()

        /**
         * Returns a [Distance] equal to [Int] number of kilometers.
         */
        public inline val Int.kilometers: Distance get() = TODO()

        /**
         * Returns a [Distance] equal to [Int] number of megameters.
         */
        public inline val Int.megameters: Distance get() = TODO()

        /**
         * Returns a [Distance] equal to [Int] number of gigameters.
         */
        public inline val Int.gigameters: Distance get() = TODO()
    }
}

public enum class DistanceUnit(internal val nanometerScale: Long) {
    Nanometer(1),
    Micrometer(1_000),
    Millimeter(1_000_000),
    Centimeter(10_000_000),
    Meter(1_000_000_000),
    Kilometer(1_000_000_000_000),
    Megameter(1_000_000_000_000_000),
    Gigameter(1_000_000_000_000_000_000),
}