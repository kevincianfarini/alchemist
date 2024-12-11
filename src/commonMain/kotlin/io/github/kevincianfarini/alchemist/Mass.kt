package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline

@JvmInline
public value class Mass(private val foo: Long) {

    /**
     * Returns a mass whose value is this mass value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Mass = TODO()

    /**
     * Returns a mass whose value is this mass value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Mass = TODO()

    /**
     * Returns the number that is the ratio of this and the [other] mass value.
     */
    public operator fun div(other: Mass): Double = TODO()

    /**
     * Returns a mass whose value is the difference between this and the [other] mass value.
     */
    public operator fun minus(other: Mass): Mass = TODO()

    /**
     * Returns a mass whose value is the sum between this and the [other] mass value.
     */
    public operator fun plus(other: Mass): Mass = TODO()

    /**
     * Returns the [Force] required to apply to this mass to achieve the specified [acceleration].
     */
    public operator fun times(acceleration: Acceleration): Force = TODO()

    /**
     * Returns a mass whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Mass = TODO()

    /**
     * Returns a mass whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Mass = TODO()

    public companion object {

    }
}

public enum class MassUnit {

}