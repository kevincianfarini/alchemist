package io.github.kevincianfarini.alchemist

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.Typography.nbsp
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class VelocityTest {

    @Test
    fun to_double_simple_works() {
        assertEquals(
            expected = 444.4444404,
            actual = (123_456_789.nanometers / 1.seconds).toDouble(
                lengthUnit = LengthUnit.International.Meter,
                durationUnit = DurationUnit.HOURS,
            ),
            absoluteTolerance = 0.000001,
        )
    }

    @Test
    fun to_string_with_units_simple_works() {
        assertEquals(
            expected = "3.6${nbsp}km/h",
            actual = (1.meters / 1.seconds).toString(
                lengthUnit = LengthUnit.International.Kilometer,
                durationUnit = DurationUnit.HOURS,
                decimals = 1,
            )
        )
    }

    @Test
    fun default_to_string_simple_works() {
        assertEquals(
            expected = "1.00${nbsp}m/s",
            actual = (1.meters / 1.seconds).toString()
        )
    }

    @Test
    fun velocity_mul_time_simple() {
        assertEquals(
            expected = 1.meters,
            actual = (1.meters / 1.seconds) * 1.seconds
        )
        assertEquals(
            expected = 444_441_600.meters,
            actual = (123_456.meters / 1.seconds) * 1.hours,
        )
    }
}