package io.github.kevincianfarini.alchemist

import io.github.kevincianfarini.alchemist.scalar.meters
import io.github.kevincianfarini.alchemist.scalar.nanometers
import io.github.kevincianfarini.alchemist.scalar.nmPerSecond2
import io.github.kevincianfarini.alchemist.unit.LengthUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.Typography.nbsp
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.nanoseconds
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
    @WasmWasiIgnore // See: https://youtrack.jetbrains.com/issue/KT-60964.
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

    @Test
    fun velocity_div_time_simple() {
        assertEquals(
            expected = 10_032_520_325.nmPerSecond2,
            actual = 1_234.nanometers / 1.seconds / 123.nanoseconds,
        )
    }
}