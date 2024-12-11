package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline
import kotlin.time.Duration

@JvmInline
public value class Energy(private val rawNanojoules: Long) {

    /**
     * Returns the constant [Force] applied over the specified [distance] required to expend this amount of energy.
     */
    public operator fun div(distance: Distance): Force = TODO()

    /**
     * Returns the constant [Power] applied over the specified [duration] to generate this amount of energy.
     */
    public operator fun div(duration: Duration): Power = TODO()

    /**
     * Returns the number that is the ratio of this and the [other] energy value.
     */
    public operator fun div(other: Energy): Double = TODO()

    /**
     * Returns an energy whose value is this energy value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Energy = TODO()

    /**
     * Returns an energy whose value is this energy value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Energy = TODO()

    /**
     * Returns an energy whose value is the difference between this and the [other] energy value.
     */
    public operator fun minus(other: Energy): Energy = TODO()

    /**
     * Returns an energy whose value is the sum between this and the [other] energy value.
     */
    public operator fun plus(other: Energy): Energy = TODO()

    /**
     * Returns an energy whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Energy = TODO()

    /**
     * Returns an energy whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Energy = TODO()

    public companion object {

    }
}

public enum class EnergyUnit {
    Nanojoule,
    Microjoule,
    Millijoule,
    Joule,
    Kilojoule,
    Megajoule,
    Gigajoule,
    Tetrajoule,
    NanowattHour,
    MicowattHour,
    MilliwattHour,
    WattHour,
    KilowattHour,
    MegawattHour,
    GigawattHour,
    TerawattHour,
}