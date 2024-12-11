package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline

@JvmInline
public value class Volume(private val foo: Long) {

    public operator fun div(area: Area): Distance = TODO()
    public operator fun div(distance: Distance): Area = TODO()
    public operator fun div(other: Volume): Double = TODO()
    public operator fun div(scale: Int): Volume = TODO()
    public operator fun div(scale: Long): Volume = TODO()
    public operator fun minus(other: Volume): Volume = TODO()
    public operator fun plus(other: Volume): Volume = TODO()
    public operator fun times(scale: Int): Volume = TODO()
    public operator fun times(scale: Long): Volume = TODO()

    public companion object {

    }
}