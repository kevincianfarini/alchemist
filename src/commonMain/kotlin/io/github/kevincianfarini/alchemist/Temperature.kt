package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline

@JvmInline
public value class Temperature(private val foo: Long) {

    /**
     * Returns the number that is the ratio of this and the [other] temperature value.
     */
    public operator fun div(other: Temperature): Double = TODO()

    /**
     * Returns a temperature whose value is this temperature value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Temperature = TODO()

    /**
     * Returns a temperature whose value is this temperature value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Temperature = TODO()

    /**
     * Returns a temperature whose value is the difference between this and the [other] temperature value.
     */
    public operator fun minus(other: Temperature): Temperature = TODO()

    /**
     * Returns a temperature whose value is the sum between this and the [other] temperature value.
     */
    public operator fun plus(other: Temperature): Temperature = TODO()

    /**
     * Returns a temperature whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Temperature = TODO()

    /**
     * Returns a temperature whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Temperature = TODO()
}

public enum class TemperatureUnit {
    Kelvin,
    Celsius,
    Fahrenheit
}