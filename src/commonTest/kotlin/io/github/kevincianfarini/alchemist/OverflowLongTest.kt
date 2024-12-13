package io.github.kevincianfarini.alchemist

import io.github.kevincianfarini.alchemist.OverflowLong.Companion.noOverflow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class OverflowLongTest {

    @Test
    fun long_max_and_min_value_is_infinite() {
        assertTrue(Long.MAX_VALUE.noOverflow.isInfinite())
        assertTrue(Long.MIN_VALUE.noOverflow.isInfinite())
    }

    @Test
    fun unary_minus_works() {
        assertEquals(1L.noOverflow, -((-1L).noOverflow))
    }

    @Test
    fun unary_minus_infinite_values() {
        assertEquals(OverflowLong.POSITIVE_INFINITY, -OverflowLong.NEGATIVE_INFINITY)
        assertEquals(OverflowLong.NEGATIVE_INFINITY, -OverflowLong.POSITIVE_INFINITY)
    }

    @Test
    fun infinite_plus_infinite_equals_infinite() {
        assertEquals(
            expected =OverflowLong.POSITIVE_INFINITY,
            actual = OverflowLong.POSITIVE_INFINITY + OverflowLong.POSITIVE_INFINITY,
        )
        assertEquals(
            expected = OverflowLong.NEGATIVE_INFINITY,
            actual = OverflowLong.NEGATIVE_INFINITY+ OverflowLong.NEGATIVE_INFINITY,
        )
    }

    @Test
    fun mixed_signed_infinite_addition_error() {
        val e = assertFailsWith<IllegalArgumentException> {
            OverflowLong.POSITIVE_INFINITY + OverflowLong.NEGATIVE_INFINITY
        }
        assertEquals(
            actual = e.message,
            expected = "Summing infinite values of different signs yields an undefined result.",
        )
    }

    @Test
    fun addition_positive_overflow_returns_positive_infinite() {
        val a = (Long.MAX_VALUE - 1L).noOverflow
        val b = 2L.noOverflow
        assertEquals(OverflowLong.POSITIVE_INFINITY, a + b)
    }

    @Test
    fun addition_negative_overflow_returns_negative_infinite() {
        val a = (Long.MIN_VALUE + 1L).noOverflow
        val b = (-2L).noOverflow
        assertEquals(OverflowLong.NEGATIVE_INFINITY, a + b)
    }

    @Test
    fun subtraction_negative_overflow_returns_negative_infinite() {
        val a = (Long.MIN_VALUE + 1L).noOverflow
        val b = 2L.noOverflow
        assertEquals(OverflowLong.NEGATIVE_INFINITY, a - b)
    }

    @Test
    fun subtraction_positive_overflow_returns_positive_infinite() {
        val a = -((Long.MAX_VALUE - 1L).noOverflow)
        val b = 2L.noOverflow
        assertEquals(OverflowLong.POSITIVE_INFINITY, b - a)
    }

    @Test
    fun adding_to_infinity_produces_infinity() {
        assertEquals(OverflowLong.POSITIVE_INFINITY, OverflowLong.POSITIVE_INFINITY + 50_000L.noOverflow)
    }

    @Test
    fun subtracting_from_negative_infinity_produces_negative_infinity() {
        assertEquals(OverflowLong.NEGATIVE_INFINITY, OverflowLong.NEGATIVE_INFINITY - 50_000L.noOverflow)
    }

    @Test
    fun dividing_works() {
        assertEquals(2L.noOverflow, 10L.noOverflow / 5L.noOverflow)
    }

    @Test
    fun dividing_two_infinite_values_errors() {
        assertFailsWith<IllegalArgumentException> {
            OverflowLong.POSITIVE_INFINITY / OverflowLong.POSITIVE_INFINITY
        }
        assertFailsWith<IllegalArgumentException> {
            OverflowLong.POSITIVE_INFINITY / OverflowLong.NEGATIVE_INFINITY
        }
        assertFailsWith<IllegalArgumentException> {
            OverflowLong.NEGATIVE_INFINITY / OverflowLong.POSITIVE_INFINITY
        }
        assertFailsWith<IllegalArgumentException> {
            OverflowLong.NEGATIVE_INFINITY / OverflowLong.NEGATIVE_INFINITY
        }
    }

    @Test
    fun dividing_by_positive_infinity_produces_zero() {
        assertEquals(0L.noOverflow, (Long.MAX_VALUE - 1L).noOverflow / OverflowLong.POSITIVE_INFINITY)
    }

    @Test
    fun dividing_by_negative_infinity_produces_zero() {
        assertEquals(0L.noOverflow, (Long.MAX_VALUE - 1L).noOverflow / OverflowLong.NEGATIVE_INFINITY)
    }

    @Test
    fun dividing_positive_infinity_by_a_value_produces_positive_infinity() {
        assertEquals(OverflowLong.POSITIVE_INFINITY, OverflowLong.POSITIVE_INFINITY / 2L)
    }

    @Test
    fun dividing_negative_infinity_by_a_value_produces_negative_infinity() {
        assertEquals(OverflowLong.NEGATIVE_INFINITY, OverflowLong.NEGATIVE_INFINITY/ 2L)
    }

    @Test
    fun multiplication_works() {
        assertEquals(10L.noOverflow, 5L.noOverflow * 2L.noOverflow)
    }

    @Test
    fun multiplfying_two_positive_infinite_values_produces_positive_infinity() {
        assertEquals(OverflowLong.POSITIVE_INFINITY, OverflowLong.POSITIVE_INFINITY * OverflowLong.POSITIVE_INFINITY)
    }

    @Test
    fun multiplying_two_negative_infinite_values_produces_positive_infinity() {
        assertEquals(OverflowLong.POSITIVE_INFINITY, OverflowLong.NEGATIVE_INFINITY * OverflowLong.NEGATIVE_INFINITY)
    }

    @Test
    fun multiplying_mixed_sign_infinite_values_produces_negative_infinity() {
        assertEquals(OverflowLong.NEGATIVE_INFINITY, OverflowLong.POSITIVE_INFINITY * OverflowLong.NEGATIVE_INFINITY)
        assertEquals(OverflowLong.NEGATIVE_INFINITY, OverflowLong.NEGATIVE_INFINITY * OverflowLong.POSITIVE_INFINITY)
    }

    @Test
    fun two_large_overflowing_positive_numbers_produces_positive_infinity() {
        val a = (Long.MAX_VALUE / 3).noOverflow
        val b = (Long.MAX_VALUE / 4).noOverflow
        assertEquals(OverflowLong.POSITIVE_INFINITY, a * b)
    }

    @Test
    fun two_large_overflowing_negative_numbers_produces_positive_infinity() {
        val a = (Long.MIN_VALUE / 3).noOverflow
        val b = (Long.MIN_VALUE / 4).noOverflow
        assertEquals(OverflowLong.POSITIVE_INFINITY, a * b)
    }

    @Test
    fun two_large_overflowing_mixed_sign_numbers_produces_negative_infinity() {
        val a = (Long.MAX_VALUE / 3).noOverflow
        val b = (Long.MIN_VALUE / 4).noOverflow
        assertEquals(OverflowLong.NEGATIVE_INFINITY, a * b)
    }
}
