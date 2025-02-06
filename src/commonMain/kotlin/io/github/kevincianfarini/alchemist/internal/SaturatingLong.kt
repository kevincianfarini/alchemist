@file:Suppress("NOTHING_TO_INLINE")

package io.github.kevincianfarini.alchemist.internal

import kotlin.jvm.JvmInline
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.math.roundToLong
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
        isInfinite() -> when {
            this == other || other.isFinite() -> this
            else -> {
                throwIllegalArgumentException("Summing infinite values of different signs yields an undefined result.")
            }
        }
        else -> {
            val a = rawValue
            val b = other.rawValue
            val result = rawValue + other.rawValue
            val didOverflow = (((a and b and result.inv()) or (a.inv() and b.inv() and result)) < 0L)
            when {
                didOverflow -> if (result > 0) NEGATIVE_INFINITY else POSITIVE_INFINITY
                else -> SaturatingLong(result)
            }
        }
    }

    inline operator fun plus(other: Long): SaturatingLong {
        return this + SaturatingLong(other)
    }

    inline operator fun minus(other: SaturatingLong): SaturatingLong = this + (-other)

    inline operator fun minus(other: Long): SaturatingLong {
        return this - SaturatingLong(other)
    }

    operator fun unaryMinus(): SaturatingLong = when(this) {
        POSITIVE_INFINITY -> NEGATIVE_INFINITY
        NEGATIVE_INFINITY -> POSITIVE_INFINITY
        else -> SaturatingLong(-rawValue)
    }

    operator fun times(other: SaturatingLong): SaturatingLong {
        return when {
            isInfinite() or other.isInfinite() -> when {
                rawValue == 0L || other.rawValue == 0L -> {
                    throwIllegalArgumentException("Multiplying an infinite value by zero yields an undefined result.")
                }
                rawValue.sign == other.rawValue.sign -> POSITIVE_INFINITY
                else -> NEGATIVE_INFINITY
            }
            else -> {
                val result = rawValue * other.rawValue
                val doesOverflow = rawValue != 0L && result / rawValue != other.rawValue
                when {
                    doesOverflow && rawValue.sign == other.rawValue.sign -> POSITIVE_INFINITY
                    doesOverflow -> NEGATIVE_INFINITY
                    else -> SaturatingLong(result)
                }
            }
        }
    }

    val sign: Int inline get() = rawValue.sign

    inline operator fun times(other: Long): SaturatingLong {
        return this * SaturatingLong(other)
    }

    inline operator fun times(other: Int): SaturatingLong {
        return times(other.toLong())
    }

    inline operator fun times(other: Double): SaturatingLong {
        val longScale = other.roundToLong()
        if (longScale.toDouble() == other) {
            return times(longScale)
        } else {
            val thisDouble = toDouble()
            val result = thisDouble * other
            return SaturatingLong(result.roundToLong())
        }
    }

    operator fun div(other: SaturatingLong): SaturatingLong {
        val thisInfinite = isInfinite()
        val otherInfinite = other.isInfinite()
        return when {
            thisInfinite && otherInfinite -> {
                throwIllegalArgumentException("Dividing two infinite values yields an undefined result.")
            }
            thisInfinite -> this * other.sign
            otherInfinite -> SaturatingLong(0)
            else -> SaturatingLong(rawValue / other.rawValue)
        }
    }

    operator fun div(other: Long): SaturatingLong {
        return this / SaturatingLong(other)
    }

    operator fun div(other: Int): SaturatingLong {
        return div(other.toLong())
    }

    operator fun div(other: Double): SaturatingLong {
        val longScale = other.roundToLong()
        if (longScale.toDouble() == other) {
            return div(longScale)
        } else {
            val thisDouble = toDouble()
            val result = thisDouble / other
            return SaturatingLong(result.roundToLong())
        }
    }

    operator fun rem(other: SaturatingLong): SaturatingLong {
        val thisInfinite = isInfinite()
        val otherInfinite = other.isInfinite()
        return when {
            thisInfinite && otherInfinite -> {
                throwIllegalArgumentException("Dividing two infinite values yields an undefined result.")
            }
            thisInfinite -> this * other.sign
            otherInfinite -> SaturatingLong(0)
            else -> SaturatingLong(rawValue % other.rawValue)
        }
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

    override fun toString(): String = when (this) {
        POSITIVE_INFINITY -> "Infinity"
        NEGATIVE_INFINITY -> "-Infinity"
        else -> rawValue.toString()
    }

    inline fun isInfinite(): Boolean {
        return (this == POSITIVE_INFINITY) or (this == NEGATIVE_INFINITY)
    }

    inline fun isFinite(): Boolean = !isInfinite()

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

// Inline the constants versus using `MIN_VALUE` and `MAX_VALUE` to avoid accessing Long's companion.
internal inline val POSITIVE_INFINITY get() = SaturatingLong(9223372036854775807L)
internal inline val NEGATIVE_INFINITY get() = SaturatingLong(-9223372036854775807L - 1L)
