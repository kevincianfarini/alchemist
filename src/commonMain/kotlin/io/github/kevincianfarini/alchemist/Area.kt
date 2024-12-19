package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline

/**
 * Represents a measure of area and is capable of storing ±9.22 million kilometers² at millimeter² precision.
 */
@JvmInline
public value class Area internal constructor(private val rawMillimetersSquared: Long) {

    /**
     * Returns the resulting distance after dividing this area by the specified [distance].
     */
    public operator fun div(distance: Distance): Distance = TODO()

    /**
     * Returns an area whose value is this area value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Area = TODO()

    /**
     * Returns an area whose value is this area value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Area = TODO()

    /**
     * Returns the number that is the ratio of this and the [other] area value.
     */
    public operator fun div(other: Area): Double = TODO()

    /**
     * Returns an area whose value is the difference between this and the [other] area value.
     */
    public operator fun minus(other: Area): Area = TODO()

    /**
     * Returns an area whose value is the sum between this and the [other] area value.
     */
    public operator fun plus(other: Area): Area = TODO()

    /**
     * Returns the resulting [Volume] after applying this area over the specified [distance].
     */
    public operator fun times(distance: Distance): Volume = TODO()

    /**
     * Returns an area whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Area = TODO()

    /**
     * Returns an area whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Area = TODO()

    public companion object {

    }
}