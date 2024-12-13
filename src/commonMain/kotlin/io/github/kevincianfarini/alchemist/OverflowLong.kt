package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline
import kotlin.math.absoluteValue
import kotlin.math.sign

@JvmInline
internal value class OverflowLong private constructor(internal val rawValue: Long) : Comparable<OverflowLong> {

    operator fun plus(other: OverflowLong): OverflowLong = when {
        isInfinite() && isPositive() && other.isInfinite() && other.isPositive() -> POSITIVE_INFINITY
        isInfinite() && isNegative() && other.isInfinite() && other.isNegative() -> NEGATIVE_INFINITY
        isInfinite() && other.isInfinite() -> {
            throw IllegalArgumentException("Summing infinite values of different signs yields an undefined result.")
        }
        else -> {
            val a = rawValue
            val b = other.rawValue
            val result = rawValue + other.rawValue
            val didOverflow = (((a and b and result.inv()) or (a.inv() and b.inv() and result)) < 0L)
            when {
                didOverflow && result > 0 -> NEGATIVE_INFINITY
                didOverflow && result < 0 -> POSITIVE_INFINITY
                else -> OverflowLong(result)
            }
        }
    }

    operator fun plus(other: Long): OverflowLong {
        return this + OverflowLong(other)
    }

    operator fun minus(other: OverflowLong): OverflowLong = this + (-other)

    operator fun minus(other: Long): OverflowLong {
        return this - OverflowLong(other)
    }

    operator fun unaryMinus(): OverflowLong = when {
        isInfinite() && isPositive() -> NEGATIVE_INFINITY
        isInfinite() && isNegative() -> POSITIVE_INFINITY
        else -> OverflowLong(-rawValue)
    }

    operator fun times(other: OverflowLong): OverflowLong {
        val max = if (rawValue.sign == other.rawValue.sign) Long.MAX_VALUE else Long.MIN_VALUE
        val a = rawValue
        val b = other.rawValue
        val doesOverflow = a != 0L && (b > 0L && b > max / a || b < 0L && b < max / a)
        return when {
            doesOverflow && a.sign == b.sign -> POSITIVE_INFINITY
            doesOverflow -> NEGATIVE_INFINITY
            else -> OverflowLong(a * b)
        }
    }

    operator fun times(other: Long): OverflowLong {
        return this * OverflowLong(other)
    }

    operator fun div(other: OverflowLong): OverflowLong = when {
        isInfinite() && other.isInfinite() -> {
            throw IllegalArgumentException("Dividing two infinite values yields an undefined result.")
        }
        isInfinite() -> this
        else -> OverflowLong(rawValue / other.rawValue)
    }

    operator fun div(other: Long): OverflowLong {
        return this / OverflowLong(other)
    }

    operator fun rem(other: OverflowLong): OverflowLong = when {
        isInfinite() && other.isInfinite() -> {
            throw IllegalArgumentException("Dividing two infinite values yields an undefined result.")
        }
        isInfinite() -> this
        else -> OverflowLong(rawValue % other.rawValue)
    }

    operator fun rem(other: Long): OverflowLong {
        return this % OverflowLong(other)
    }

    override fun compareTo(other: OverflowLong): Int {
        return rawValue.compareTo(other.rawValue)
    }

    operator fun compareTo(other: Long): Int {
        return compareTo(OverflowLong(other))
    }

    operator fun compareTo(other: Int): Int {
        return compareTo(OverflowLong(other.toLong()))
    }

    override fun toString(): String = when {
        isInfinite() && isPositive() -> "Infinity"
        isInfinite() && isNegative() -> "-Infinity"
        else -> rawValue.toString()
    }

    fun isInfinite(): Boolean {
        return this == POSITIVE_INFINITY || this == NEGATIVE_INFINITY
    }

    fun isFinite(): Boolean = !isInfinite()

    private fun isPositive(): Boolean = rawValue > 0

    private fun isNegative(): Boolean = rawValue < 0

    val absoluteValue: OverflowLong get() = when {
        isInfinite() -> POSITIVE_INFINITY
        else -> OverflowLong(rawValue.absoluteValue)
    }

    fun toDouble(): Double = when (this) {
        POSITIVE_INFINITY -> Double.POSITIVE_INFINITY
        NEGATIVE_INFINITY -> Double.NEGATIVE_INFINITY
        else -> rawValue.toDouble()
    }

    companion object {
        inline val Long.noOverflow get() = OverflowLong(this)
        val POSITIVE_INFINITY = OverflowLong(Long.MAX_VALUE)
        val NEGATIVE_INFINITY = OverflowLong(Long.MIN_VALUE)
    }
}