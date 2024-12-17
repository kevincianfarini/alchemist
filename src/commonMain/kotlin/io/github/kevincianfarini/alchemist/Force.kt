package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline

@JvmInline
public value class Force internal constructor(private val foo: Long) {

    /**
     * Returns the number that is the ratio of this and the [other] force value.
     */
    public operator fun div(other: Force): Long = TODO()

    /**
     * Returns a force whose value is this force value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Force = TODO()

    /**
     * Returns a force whose value is this force value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Force = TODO()

    /**
     * Returns the resulting [Acceleration] from applying this force to the specified [mass].
     */
    public operator fun div(mass: Mass): Acceleration = TODO()

    /**
     * Returns a force whose value is the difference between this and the [other] force value.
     */
    public operator fun minus(other: Force): Force = TODO()

    /**
     * Returns a force whose value is the sum between this and the [other] force value.
     */
    public operator fun plus(other: Force): Force = TODO()

    /**
     * Returns the amount of [Energy] required to apply this force over the specified [distance].
     */
    public operator fun times(distance: Distance): Energy = TODO()

    /**
     * Returns a force whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Force = TODO()

    /**
     * Returns a force whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Force = TODO()

    public companion object {

    }
}