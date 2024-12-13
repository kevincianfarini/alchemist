package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline
import kotlin.math.sign

@JvmInline
internal value class OverflowLong private constructor(private val value: Long) : Comparable<OverflowLong> {

    operator fun plus(other: OverflowLong): OverflowLong = when {
        isInfinite() && isPositive() && other.isInfinite() && other.isPositive() -> POSITIVE_INFINITY
        isInfinite() && isNegative() && other.isInfinite() && other.isNegative() -> NEGATIVE_INFINITY
        isInfinite() && other.isInfinite() -> {
            throw IllegalArgumentException("Summing infinite values of different signs yields an undefined result.")
        }
        else -> {
            val a = value
            val b = other.value
            val result = value + other.value
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
        else -> OverflowLong(-value)
    }

    operator fun times(other: OverflowLong): OverflowLong {
        val max = if (value.sign == other.value.sign) Long.MAX_VALUE else Long.MIN_VALUE
        val a = value
        val b = other.value
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
        else -> OverflowLong(value / other.value)
    }

    operator fun div(other: Long): OverflowLong {
        return this / OverflowLong(other)
    }

    operator fun rem(other: OverflowLong): OverflowLong {
        return OverflowLong(value % other.value)
    }

    operator fun rem(other: Long): OverflowLong {
        return this % OverflowLong(other)
    }

    override fun compareTo(other: OverflowLong): Int {
        return value.compareTo(other.value)
    }

    operator fun compareTo(other: Long): Int {
        return compareTo(OverflowLong(other))
    }

    operator fun compareTo(other: Int): Int {
        return compareTo(OverflowLong(other.toLong()))
    }

    override fun toString(): String = when {
        isInfinite() && isPositive() -> "INFINITE"
        isInfinite() && isNegative() -> "-INFINITE"
        else -> value.toString()
    }

    fun isInfinite(): Boolean {
        return this == POSITIVE_INFINITY || this == NEGATIVE_INFINITY
    }

    private fun isPositive(): Boolean = value > 0

    private fun isNegative(): Boolean = value < 0

    companion object {
        inline val Long.noOverflow get() = OverflowLong(this)
        val POSITIVE_INFINITY = OverflowLong(Long.MAX_VALUE)
        val NEGATIVE_INFINITY = OverflowLong(Long.MIN_VALUE)
    }
}