package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline
import kotlin.time.Duration

@JvmInline
public value class Velocity internal constructor(private val rawNanometerPerNanosecond: Long) {

    /**
     * Returns the [Acceleration] necessary to achieve this velocity when applied over the specified [duration].
     */
    public operator fun div(duration: Duration): Acceleration = TODO()

    /**
     * Returns the number that is the ratio of this and the [other] velocity value.
     */
    public operator fun div(other: Velocity): Double = TODO()

    /**
     * Returns a velocity whose value is this velocity value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Velocity = TODO()

    /**
     * Returns a velocity whose value is this velocity value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Velocity = TODO()

    /**
     * Returns a velocity whose value is the difference between this and the [other] velocity value.
     */
    public operator fun minus(other: Velocity): Velocity = TODO()

    /**
     * Returns a velocity whose value is the sum between this and the [other] velocity value.
     */
    public operator fun plus(other: Velocity): Velocity = TODO()

    /**
     * Returns the [Distance] traveled at this velocity for the specified [duration].
     */
    public operator fun times(duration: Duration): Distance = TODO()

    /**
     * Returns a velocity whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Velocity = TODO()

    /**
     * Returns a velocity whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Velocity = TODO()

    public companion object {

    }
}