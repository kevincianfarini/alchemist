package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline
import kotlin.time.Duration

@JvmInline
public value class Power(private val rawNanowatts: Long) {

    /**
     * Returns the number that is the ratio of this and the [other] power value.
     */
    public operator fun div(other: Power): Double = TODO()

    /**
     * Returns a power whose value is this power value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Power = TODO()

    /**
     * Returns a power whose value is this power value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Power = TODO()

    /**
     * Returns a power whose value is the difference between this and the [other] power value.
     */
    public operator fun minus(other: Power): Power = TODO()

    /**
     * Returns a power whose value is the sum between this and the [other] power value.
     */
    public operator fun plus(other: Power): Power = TODO()

    /**
     * Returns the resulting [Energy] from applying this power over the specified [duration].
     */
    public operator fun times(duration: Duration): Energy = TODO()

    /**
     * Returns a power whose value is this power multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Power = TODO()

    /**
     * Returns a power whose value is this power multiplied by the specified [scale].
     */
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