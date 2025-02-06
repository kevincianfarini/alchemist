package io.github.kevincianfarini.alchemist

import io.github.kevincianfarini.alchemist.internal.NEGATIVE_INFINITY
import io.github.kevincianfarini.alchemist.internal.POSITIVE_INFINITY
import io.github.kevincianfarini.alchemist.internal.saturated
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SaturatingLongTest {

    @Test
    fun long_max_and_min_value_is_infinite() {
        assertTrue(Long.MAX_VALUE.saturated.isInfinite())
        assertTrue(Long.MIN_VALUE.saturated.isInfinite())
    }

    @Test
    fun unary_minus_works() {
        assertEquals(1L.saturated, -((-1L).saturated))
    }

    @Test
    fun unary_minus_infinite_values() {
        assertEquals(POSITIVE_INFINITY, -NEGATIVE_INFINITY)
        assertEquals(NEGATIVE_INFINITY, -POSITIVE_INFINITY)
    }

    @Test
    fun infinite_plus_infinite_equals_infinite() {
        assertEquals(
            expected = POSITIVE_INFINITY,
            actual = POSITIVE_INFINITY + POSITIVE_INFINITY,
        )
        assertEquals(
            expected = NEGATIVE_INFINITY,
            actual = NEGATIVE_INFINITY+ NEGATIVE_INFINITY,
        )
    }

    @Test
    fun mixed_signed_infinite_addition_error() {
        val e = assertFailsWith<IllegalArgumentException> {
            POSITIVE_INFINITY + NEGATIVE_INFINITY
        }
        assertEquals(
            actual = e.message,
            expected = "Summing infinite values of different signs yields an undefined result.",
        )
    }

    @Test
    fun addition_positive_overflow_returns_positive_infinite() {
        val a = (Long.MAX_VALUE - 1L).saturated
        val b = 2L.saturated
        assertEquals(POSITIVE_INFINITY, a + b)
    }

    @Test
    fun addition_negative_overflow_returns_negative_infinite() {
        val a = (Long.MIN_VALUE + 1L).saturated
        val b = (-2L).saturated
        assertEquals(NEGATIVE_INFINITY, a + b)
    }

    @Test
    fun subtraction_negative_overflow_returns_negative_infinite() {
        val a = (Long.MIN_VALUE + 1L).saturated
        val b = 2L.saturated
        assertEquals(NEGATIVE_INFINITY, a - b)
    }

    @Test
    fun subtraction_positive_overflow_returns_positive_infinite() {
        val a = -((Long.MAX_VALUE - 1L).saturated)
        val b = 2L.saturated
        assertEquals(POSITIVE_INFINITY, b - a)
    }

    @Test
    fun adding_to_infinity_produces_infinity() {
        assertEquals(POSITIVE_INFINITY, POSITIVE_INFINITY + 50_000L.saturated)
    }

    @Test
    fun adding_to_negative_infinity_produces_negative_infinity() {
        assertEquals(NEGATIVE_INFINITY, NEGATIVE_INFINITY + 50_000L.saturated)
    }

    @Test
    fun subtracting_from_positive_infinity_produces_positive_infinity() {
        assertEquals(POSITIVE_INFINITY, POSITIVE_INFINITY - 50_000L.saturated)
    }

    @Test
    fun subtracting_from_negative_infinity_produces_negative_infinity() {
        assertEquals(NEGATIVE_INFINITY, NEGATIVE_INFINITY - 50_000L.saturated)
    }

    @Test
    fun dividing_works() {
        assertEquals(2L.saturated, 10L.saturated / 5L.saturated)
    }

    @Test
    fun dividing_two_infinite_values_errors() {
        assertFailsWith<IllegalArgumentException> {
            POSITIVE_INFINITY / POSITIVE_INFINITY
        }
        assertFailsWith<IllegalArgumentException> {
            POSITIVE_INFINITY / NEGATIVE_INFINITY
        }
        assertFailsWith<IllegalArgumentException> {
            NEGATIVE_INFINITY / POSITIVE_INFINITY
        }
        assertFailsWith<IllegalArgumentException> {
            NEGATIVE_INFINITY / NEGATIVE_INFINITY
        }
    }

    @Test
    fun dividing_by_positive_infinity_produces_zero() {
        assertEquals(0L.saturated, (Long.MAX_VALUE - 1L).saturated / POSITIVE_INFINITY)
    }

    @Test
    fun dividing_by_negative_infinity_produces_zero() {
        assertEquals(0L.saturated, (Long.MAX_VALUE - 1L).saturated / NEGATIVE_INFINITY)
    }

    @Test
    fun dividing_positive_infinity_by_a_positive_value_produces_positive_infinity() {
        assertEquals(POSITIVE_INFINITY, POSITIVE_INFINITY / 2L)
    }

    @Test
    fun dividing_positive_infinity_by_negative_value_produces_negative_infinity() {
        assertEquals(NEGATIVE_INFINITY, POSITIVE_INFINITY / -1L)
    }

    @Test
    fun dividing_negative_infinity_by_a_positive_value_produces_negative_infinity() {
        assertEquals(NEGATIVE_INFINITY, NEGATIVE_INFINITY / 2L)
    }

    @Test
    fun dividing_negative_infinity_by_negative_value_produces_positive_infinity() {
        assertEquals(POSITIVE_INFINITY, NEGATIVE_INFINITY / -1L)
    }

    @Test
    fun dividing_finite_value_by_infinite_value_produces_zero() {
        assertEquals(0L.saturated, (Long.MAX_VALUE - 1).saturated / POSITIVE_INFINITY)
        assertEquals(0L.saturated, (Long.MAX_VALUE - 1).saturated / NEGATIVE_INFINITY)
        assertEquals(0L.saturated, (Long.MIN_VALUE + 1).saturated / POSITIVE_INFINITY)
        assertEquals(0L.saturated, (Long.MIN_VALUE + 1).saturated / NEGATIVE_INFINITY)
    }

    @Test
    fun multiplication_works() {
        assertEquals(10L.saturated, 5L.saturated * 2L.saturated)
    }

    @Test
    fun multiplfying_two_positive_infinite_values_produces_positive_infinity() {
        assertEquals(POSITIVE_INFINITY, POSITIVE_INFINITY * POSITIVE_INFINITY)
    }

    @Test
    fun multiplying_two_negative_infinite_values_produces_positive_infinity() {
        assertEquals(POSITIVE_INFINITY, NEGATIVE_INFINITY * NEGATIVE_INFINITY)
    }

    @Test
    fun multiplying_mixed_sign_infinite_values_produces_negative_infinity() {
        assertEquals(NEGATIVE_INFINITY, POSITIVE_INFINITY * NEGATIVE_INFINITY)
        assertEquals(NEGATIVE_INFINITY, NEGATIVE_INFINITY * POSITIVE_INFINITY)
    }

    @Test
    fun two_large_overflowing_positive_numbers_produces_positive_infinity() {
        val a = (Long.MAX_VALUE / 3).saturated
        val b = (Long.MAX_VALUE / 4).saturated
        assertEquals(POSITIVE_INFINITY, a * b)
    }

    @Test
    fun two_large_overflowing_negative_numbers_produces_positive_infinity() {
        val a = (Long.MIN_VALUE / 3).saturated
        val b = (Long.MIN_VALUE / 4).saturated
        assertEquals(POSITIVE_INFINITY, a * b)
    }

    @Test
    fun two_large_overflowing_mixed_sign_numbers_produces_negative_infinity() {
        val a = (Long.MAX_VALUE / 3).saturated
        val b = (Long.MIN_VALUE / 4).saturated
        assertEquals(NEGATIVE_INFINITY, a * b)
    }

    @Test
    fun aboslute_value_infinity_produces_infinity() {
        assertEquals(POSITIVE_INFINITY, POSITIVE_INFINITY.absoluteValue)
        assertEquals(POSITIVE_INFINITY, NEGATIVE_INFINITY.absoluteValue)
    }

    @Test
    fun absolute_value_works() {
        assertEquals(10L.saturated, (-10L).saturated.absoluteValue)
        assertEquals(10L.saturated, 10L.saturated.absoluteValue)
    }

    @Test
    fun infinity_times_infinity_preserves_sign() {
        assertEquals(POSITIVE_INFINITY, POSITIVE_INFINITY * POSITIVE_INFINITY)
        assertEquals(NEGATIVE_INFINITY, NEGATIVE_INFINITY * POSITIVE_INFINITY)
        assertEquals(NEGATIVE_INFINITY, POSITIVE_INFINITY * NEGATIVE_INFINITY)
        assertEquals(POSITIVE_INFINITY, NEGATIVE_INFINITY * NEGATIVE_INFINITY)
    }

    @Test
    fun infinity_times_zero_throws() {
        assertFailsWith<IllegalArgumentException> {
            POSITIVE_INFINITY * 0
        }
        assertFailsWith<IllegalArgumentException> {
            NEGATIVE_INFINITY * 0
        }
    }

    @Test
    fun does_not_overflow_mul_negative_one() {
        assertEquals(
            expected = (-1_000_000_000_000_000_000).saturated,
            actual = (-1L).saturated * 1_000_000_000_000_000_000,
        )
    }

    @Test
    fun multiplication_overflow_does_not_divide_by_zero() {
        assertEquals(0L.saturated, 0L.saturated * (Long.MAX_VALUE - 1))
    }

    @Test
    fun multiplying_by_nan_throws() {
        assertFailsWith<IllegalArgumentException> {
            10L.saturated * Double.NaN
        }
    }

    @Test
    fun multiplying_by_double_infinity_preserves_sign() {
        assertEquals(POSITIVE_INFINITY, 10L.saturated * Double.POSITIVE_INFINITY)
        assertEquals(NEGATIVE_INFINITY, 10L.saturated * Double.NEGATIVE_INFINITY)
        assertEquals(NEGATIVE_INFINITY, (-10L).saturated * Double.POSITIVE_INFINITY)
        assertEquals(POSITIVE_INFINITY, (-10L).saturated * Double.NEGATIVE_INFINITY)
    }

    @Test
    fun multiplying_infinity_by_double_preserves_sign() {
        assertEquals(POSITIVE_INFINITY, POSITIVE_INFINITY * 2.0)
        assertEquals(NEGATIVE_INFINITY, NEGATIVE_INFINITY * 2.0)
        assertEquals(NEGATIVE_INFINITY, POSITIVE_INFINITY * -2.0)
        assertEquals(POSITIVE_INFINITY, NEGATIVE_INFINITY * -2.0)
    }

    @Test
    fun multiplying_by_double_zero_throws_for_infinity() {
        assertFailsWith<IllegalArgumentException> {
            POSITIVE_INFINITY * 0.0
        }
        assertFailsWith<IllegalArgumentException> {
            NEGATIVE_INFINITY * 0.0
        }
    }

    @Test
    fun multiplying_by_double_overflows_correctly() {
        assertEquals(POSITIVE_INFINITY, (Long.MAX_VALUE / 2).saturated * 2.1)
        assertEquals(NEGATIVE_INFINITY, (Long.MAX_VALUE / 2).saturated * -2.1)
        assertEquals(NEGATIVE_INFINITY, (Long.MIN_VALUE / 2).saturated * 2.1)
        assertEquals(POSITIVE_INFINITY, (Long.MIN_VALUE / 2).saturated * -2.1)
    }

    @Test
    fun multiplying_by_double_uses_integer_path_when_possible() {
        val bigLong = 92233720368547758L

        // assert that this Long is not exactly representable as a Double
        assertNotEquals(bigLong, bigLong.toDouble().toLong())

        // assert that the integer path is used
        assertEquals((bigLong * 2L).saturated, bigLong.saturated * 2.0)
    }

    @Test
    fun multiplying_by_double_uses_float_path_when_necessary() {
      val bigLong = 92233720368547758L

      // assert that this Long is not exactly representable as a Double
      assertNotEquals(bigLong, bigLong.toDouble().toLong())

      // assert that the float path is used
      assertEquals((bigLong.toDouble()*0.3).toLong().saturated, bigLong.saturated * 0.3)
    }

    @Test
    fun dividing_by_double_nan_throws() {
        assertFailsWith<IllegalArgumentException> {
            10L.saturated / Double.NaN
        }
    }

    @Test
    fun dividing_by_double_infinity_preserves_sign() {
        assertEquals(0L.saturated, 10L.saturated / Double.POSITIVE_INFINITY)
        assertEquals(0L.saturated, 10L.saturated / Double.NEGATIVE_INFINITY)
        assertEquals(0L.saturated, (-10L).saturated / Double.POSITIVE_INFINITY)
        assertEquals(0L.saturated, (-10L).saturated / Double.NEGATIVE_INFINITY)
    }

    @Test
    fun dividing_infinity_by_double_preserves_sign() {
        assertEquals(POSITIVE_INFINITY, POSITIVE_INFINITY / 2.0)
        assertEquals(NEGATIVE_INFINITY, NEGATIVE_INFINITY / 2.0)
        assertEquals(NEGATIVE_INFINITY, POSITIVE_INFINITY / -2.0)
        assertEquals(POSITIVE_INFINITY, NEGATIVE_INFINITY / -2.0)
    }

    @Test
    fun dividing_by_double_zero_throws() {
        assertFailsWith<Exception> { // ArithmeticException on most platforms; Exception on JS
            10L.saturated / 0.0
        }
        assertFailsWith<IllegalArgumentException> {
            POSITIVE_INFINITY / 0.0
        }
        assertFailsWith<IllegalArgumentException> {
            NEGATIVE_INFINITY / 0.0
        }
    }

    @Test
    fun dividing_by_double_uses_integer_path_when_possible() {
        val bigLong = 92233720368547758L

        // assert that this Long is not exactly representable as a Double
        assertNotEquals(bigLong, bigLong.toDouble().toLong())

        // assert that the integer path is used
        assertEquals((bigLong / 2L).saturated, bigLong.saturated / 2.0)
    }

    @Test
    fun dividing_by_double_uses_float_path_when_necessary() {
        val bigLong = 92233720368547758L

        // assert that this Long is not exactly representable as a Double
        assertNotEquals(bigLong, bigLong.toDouble().toLong())

        // assert that the float path is used
        assertEquals((bigLong.toDouble() / 0.3).toLong().saturated, bigLong.saturated / 0.3)
    }
}
