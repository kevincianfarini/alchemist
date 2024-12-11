package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline
import kotlin.time.Duration

@JvmInline
public value class Acceleration internal constructor(private val rawNanometersPerNanosecondSquared: Long) {

    /**
     * Returns the number that is the ratio of this and the [other] acceleration value.
     */
    public operator fun div(other: Acceleration): Double = TODO()

    /**
     * Returns an acceleration whose value is this acceleration value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Acceleration = TODO()

    /**
     * Returns an acceleration whose value is this acceleration value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Acceleration = TODO()

    /**
     * Returns an acceleration whose value is the difference between this and the [other] acceleration value.
     */
    public operator fun minus(other: Acceleration): Acceleration = TODO()

    /**
     * Returns an acceleration whose value is the sum between this and the [other] acceleration value.
     */
    public operator fun plus(other: Acceleration): Acceleration = TODO()

    /**
     * Returns the resulting [Velocity] after applying this acceleration for [duration].
     */
    public operator fun times(duration: Duration): Velocity = TODO()

    /**
     * Returns the resulting [Force] after applying this acceleration to [mass].
     */
    public operator fun times(mass: Mass): Force = TODO()

    /**
     * Returns an acceleration whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Acceleration = TODO()

    /**
     * Returns an acceleration whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Acceleration = TODO()

    public companion object {

    }
}