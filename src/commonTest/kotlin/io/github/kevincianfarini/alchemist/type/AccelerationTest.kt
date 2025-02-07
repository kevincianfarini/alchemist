package io.github.kevincianfarini.alchemist.type

import io.github.kevincianfarini.alchemist.WasmWasiIgnore
import io.github.kevincianfarini.alchemist.scalar.grams
import io.github.kevincianfarini.alchemist.scalar.nanonewtons
import io.github.kevincianfarini.alchemist.scalar.nmPerSecond
import io.github.kevincianfarini.alchemist.scalar.nmPerSecond2
import io.github.kevincianfarini.alchemist.unit.LengthUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.Typography.nbsp
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.DurationUnit

class AccelerationTest {

    @Test
    @WasmWasiIgnore // See: https://youtrack.jetbrains.com/issue/KT-60964.
    fun simple() {
        assertEquals(
            expected = "1.00${nbsp}m/s²",
            actual = 1_000_000_000.nmPerSecond2.toString()
        )
    }

    @Test
    fun less_simple() {
        assertEquals(
            expected = "8052.97${nbsp}mi/h²",
            actual = 1_000_000_000.nmPerSecond2.toString(
                lengthUnit = LengthUnit.UnitedStatesCustomary.Mile,
                durationUnit = DurationUnit.HOURS,
                decimals = 2,
            )
        )
    }

    @Test
    fun acceleration_mul_time_simple() {
        assertEquals(
            expected = 12_340_000_000.nmPerSecond,
            actual = 10_000_000_000.nmPerSecond2 * 1_234.milliseconds,
        )
    }

    @Test
    fun acceleration_mul_mass_simple() {
        // 152345677.626 nanonewtons, but we lose precision.
        assertEquals(
            expected = 152_345_677.nanonewtons,
            actual = 123_456_789.nmPerSecond2 * 1_234.grams,
        )
    }
}