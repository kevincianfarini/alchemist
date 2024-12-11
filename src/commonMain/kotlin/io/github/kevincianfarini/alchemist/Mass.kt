package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline

@JvmInline
public value class Mass(private val foo: Long) {

    public operator fun div(scale: Int): Mass = TODO()
    public operator fun div(scale: Long): Mass = TODO()
    public operator fun div(other: Mass): Double = TODO()
    public operator fun minus(other: Mass): Mass = TODO()
    public operator fun plus(other: Mass): Mass = TODO()
    public operator fun times(acceleration: Acceleration): Force = TODO()
    public operator fun times(scale: Int): Mass = TODO()
    public operator fun times(scale: Long): Mass = TODO()

    public companion object {

    }
}

public enum class MassUnit {

}