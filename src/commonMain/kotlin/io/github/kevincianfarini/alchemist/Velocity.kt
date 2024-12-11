package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline
import kotlin.time.Duration

@JvmInline
public value class Velocity(private val rawNanometerPerNanosecond: Long) {

    public operator fun div(duration: Duration): Acceleration = TODO()
    public operator fun div(other: Velocity): Double = TODO()
    public operator fun div(scale: Int): Velocity = TODO()
    public operator fun div(scale: Long): Velocity = TODO()
    public operator fun minus(other: Velocity): Velocity = TODO()
    public operator fun plus(other: Velocity): Velocity = TODO()
    public operator fun times(duration: Duration): Distance = TODO()
    public operator fun times(scale: Int): Velocity = TODO()
    public operator fun times(scale: Long): Velocity = TODO()

    public companion object {

    }
}