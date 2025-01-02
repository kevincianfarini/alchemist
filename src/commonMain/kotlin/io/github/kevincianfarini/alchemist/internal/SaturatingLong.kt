package io.github.kevincianfarini.alchemist.internal

import kotlin.jvm.JvmInline
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 * A [SaturatingLong] is a 64-bit signed integer that protects against overflow during multiplication, addition, and
 * subtraction. When one of those operations overflows, either [POSITIVE_INFINITY] or [NEGATIVE_INFINITY] is returned.
 * Performing operations with an infinite value will either return another infinite value if it's a valid operation,
 * or will error if it's an invalid operation.
 *
 * See [Saturation Arithmetic Intrinsics](https://llvm.org/docs/LangRef.html#saturation-arithmetic-intrinsics) from
 * LLVM for more details.
 */
@JvmInline
internal value class SaturatingLong(val rawValue: Long) {

    operator fun plus(other: SaturatingLong): SaturatingLong = when {
        isInfinite() && isPositive() && other.isInfinite() && other.isPositive() -> POSITIVE_INFINITY
        isInfinite() && isNegative() && other.isInfinite() && other.isNegative() -> NEGATIVE_INFINITY
        isInfinite() && other.isFinite() -> this
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
                else -> SaturatingLong(result)
            }
        }
    }

    operator fun plus(other: Long): SaturatingLong {
        return this + SaturatingLong(other)
    }

    operator fun minus(other: SaturatingLong): SaturatingLong = this + (-other)

    operator fun minus(other: Long): SaturatingLong {
        return this - SaturatingLong(other)
    }

    operator fun unaryMinus(): SaturatingLong = when {
        isInfinite() && isPositive() -> NEGATIVE_INFINITY
        isInfinite() && isNegative() -> POSITIVE_INFINITY
        else -> SaturatingLong(-rawValue)
    }

    operator fun times(other: SaturatingLong): SaturatingLong = when {
        isInfinite() || other.isInfinite() -> when (rawValue.sign * other.rawValue.sign) {
            1 -> POSITIVE_INFINITY
            -1 -> NEGATIVE_INFINITY
            else -> throw IllegalArgumentException("Multiplying an infinite value by zero yields an undefined result.")
        }
        else -> {
            val result = rawValue * other.rawValue
            val doesOverflow = rawValue != 0L && result / rawValue != other.rawValue
            when {
                doesOverflow && sign == other.rawValue.sign -> POSITIVE_INFINITY
                doesOverflow -> NEGATIVE_INFINITY
                else -> SaturatingLong(result)
            }
        }
    }

    val sign: Int get() = rawValue.sign

    operator fun times(other: Long): SaturatingLong {
        return this * SaturatingLong(other)
    }

    operator fun times(other: Int): SaturatingLong {
        return times(other.toLong())
    }

    operator fun div(other: SaturatingLong): SaturatingLong = when {
        isInfinite() && other.isInfinite() -> {
            throw IllegalArgumentException("Dividing two infinite values yields an undefined result.")
        }
        isInfinite() -> this * other.sign
        other.isInfinite() -> SaturatingLong(0)
        else -> SaturatingLong(rawValue / other.rawValue)
    }

    operator fun div(other: Long): SaturatingLong {
        return this / SaturatingLong(other)
    }

    operator fun div(other: Int): SaturatingLong {
        return div(other.toLong())
    }

    operator fun rem(other: SaturatingLong): SaturatingLong = when {
        isInfinite() && other.isInfinite() -> {
            throw IllegalArgumentException("Dividing two infinite values yields an undefined result.")
        }
        isInfinite() -> this * other.sign
        other.isInfinite() -> SaturatingLong(0)
        else -> SaturatingLong(rawValue % other.rawValue)
    }

    operator fun rem(other: Long): SaturatingLong {
        return this % SaturatingLong(other)
    }

    operator fun compareTo(other: SaturatingLong): Int {
        return rawValue.compareTo(other.rawValue)
    }

    operator fun compareTo(other: Long): Int {
        return compareTo(SaturatingLong(other))
    }

    operator fun compareTo(other: Int): Int {
        return compareTo(SaturatingLong(other.toLong()))
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

    val absoluteValue: SaturatingLong
        get() = when {
        isInfinite() -> POSITIVE_INFINITY
        else -> SaturatingLong(rawValue.absoluteValue)
    }

    fun toDouble(): Double = when (this) {
        POSITIVE_INFINITY -> Double.POSITIVE_INFINITY
        NEGATIVE_INFINITY -> Double.NEGATIVE_INFINITY
        else -> rawValue.toDouble()
    }
}

internal inline val Long.saturated get() = SaturatingLong(this)
internal inline val POSITIVE_INFINITY get() = SaturatingLong(Long.MAX_VALUE)
internal inline val NEGATIVE_INFINITY get() = SaturatingLong(Long.MIN_VALUE)
