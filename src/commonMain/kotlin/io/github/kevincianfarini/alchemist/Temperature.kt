package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline

@JvmInline
public value class Temperature(private val rawKelvin: Double) {

    public operator fun div(other: Temperature): Double = TODO()
    public operator fun div(scale: Int): Temperature = TODO()
    public operator fun div(scale: Long): Temperature = TODO()
    public operator fun minus(other: Temperature): Temperature = TODO()
    public operator fun plus(other: Temperature): Temperature = TODO()
    public operator fun times(scale: Int): Temperature = TODO()
    public operator fun times(scale: Long): Temperature = TODO()
}

public enum class TemperatureUnit {
    Kelvin, Celsius, Fahrenheit
}