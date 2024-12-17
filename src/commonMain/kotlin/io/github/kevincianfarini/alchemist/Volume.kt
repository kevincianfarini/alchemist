package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline

@JvmInline
public value class Volume internal constructor(private val foo: Long) {

    /**
     * Returns the resulting [Distance] after dividing this volume over the specified [area].
     */
    public operator fun div(area: Area): Distance = TODO()

    /**
     * Returns the resulting [Area] after dividing this volume over the specified [distance].
     */
    public operator fun div(distance: Distance): Area = TODO()

    /**
     * Returns the number that is the ratio of this and the [other] volume value.
     */
    public operator fun div(other: Volume): Double = TODO()

    /**
     * Returns a volume whose value is this volume value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Volume = TODO()

    /**
     * Returns a volume whose value is this volume value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Volume = TODO()

    /**
     * Returns a volume whose value is the difference between this and the [other] volume value.
     */
    public operator fun minus(other: Volume): Volume = TODO()

    /**
     * Returns a volume whose value is the sum between this and the [other] volume value.
     */
    public operator fun plus(other: Volume): Volume = TODO()

    /**
     * Returns a volume whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Volume = TODO()

    /**
     * Returns a volume whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Volume = TODO()

    public companion object {

    }
}