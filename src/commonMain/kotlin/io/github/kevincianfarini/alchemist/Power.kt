package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline
import kotlin.time.Duration

@JvmInline
public value class Power(private val rawNanowatts: Long) {

    public operator fun div(other: Power): Double = TODO()
    public operator fun div(scale: Int): Power = TODO()
    public operator fun div(scale: Long): Power = TODO()
    public operator fun minus(other: Power): Power = TODO()
    public operator fun plus(other: Power): Power = TODO()
    public operator fun times(duration: Duration): Energy = TODO()
    public operator fun times(scale: Int): Power = TODO()
    public operator fun times(scale: Long): Power = TODO()

    public companion object {

    }
}

public enum class PowerUnit {
    Nanowatt,
    Microwatt,
    Milliwatt,
    Watt,
    Kilowatt,
    Megawatt,
    Gigawatt,
    Terawatt,
}