package io.github.kevincianfarini.alchemist

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class PowerTest {

    @Test
    fun power_multiply_infinite_time() {
        assertEquals(
            expected = Energy.POSITIVE_INFINITY,
            actual = 1.microwatts * Duration.INFINITE,
        )
        assertEquals(
            expected = Energy.NEGATIVE_INFINITY,
            actual = (-1).microwatts * Duration.INFINITE,
        )
        assertEquals(
            expected = Energy.POSITIVE_INFINITY,
            actual = (-1).microwatts * -Duration.INFINITE,
        )
        assertEquals(
            expected = Energy.NEGATIVE_INFINITY,
            actual = 1.microwatts * -Duration.INFINITE,
        )
    }

    @Test
    fun infinite_power_multiply_time() {
        assertEquals(
            expected = Energy.POSITIVE_INFINITY,
            actual = Power.POSITIVE_INFINITY * 1.nanoseconds,
        )
        assertEquals(
            expected = Energy.POSITIVE_INFINITY,
            actual = Power.NEGATIVE_INFINITY * (-1).nanoseconds,
        )
        assertEquals(
            expected = Energy.NEGATIVE_INFINITY,
            actual = Power.NEGATIVE_INFINITY * 1.nanoseconds,
        )
        assertEquals(
            expected = Energy.NEGATIVE_INFINITY,
            actual = Power.POSITIVE_INFINITY * (-1).nanoseconds,
        )
    }

    @Test
    fun infinite_power_multiply_infinite_time() {
        assertEquals(
            expected = Energy.POSITIVE_INFINITY,
            actual = Power.POSITIVE_INFINITY * Duration.INFINITE,
        )
        assertEquals(
            expected = Energy.POSITIVE_INFINITY,
            actual = Power.NEGATIVE_INFINITY * -Duration.INFINITE,
        )
        assertEquals(
            expected = Energy.NEGATIVE_INFINITY,
            actual = Power.NEGATIVE_INFINITY * Duration.INFINITE,
        )
        assertEquals(
            expected = Energy.NEGATIVE_INFINITY,
            actual = Power.POSITIVE_INFINITY * -Duration.INFINITE,
        )
    }

    @Test
    fun power_multiply_time_precision_femtojoule_precision() {
        assertEquals(
            expected = 1_234.millijoules,
            actual = 1_234.microwatts * 1_000.seconds
        )
    }

    @Test
    fun power_multiply_time_precision_picojoule_precision() {
        // 123,456,788.999074074 millijoules, but we lose precision.
        assertEquals(
            expected = 123_456_788.millijoules,
            actual = 124_999_998_860.microwatts * 987_654_321.nanoseconds,
        )
    }

    @Test
    fun power_multiply_time_precision_nanojoule_precision() {
        // 123,456,788,999.074074 millijoules, but we lose precision.
        assertEquals(
            expected = 123_456_788_999.millijoules,
            actual = 124_999_998_860.microwatts * 987_654_321.microseconds,
        )
    }

    @Test
    fun power_multiply_time_precision_microjoule_precision() {
        // 123,456,788,999,074.07406 millijoules, but we lose precision.
        assertEquals(
            expected = 123_456_788_999_074.millijoules,
            actual = 124_999_998_860.microwatts * 987_654_321.milliseconds,
        )
    }

    @Test
    fun power_multiply_time_precision_millijoule_precision() {
        // 123,456,788,999,074,074.06 millijoules, but we lose precision.
        assertEquals(
            expected = 123_456_788_999_074_074.millijoules,
            actual = 124_999_998_860.microwatts * 987_654_321.seconds,
        )
    }
}