package io.github.kevincianfarini.alchemist.type

import io.github.kevincianfarini.alchemist.WasmWasiIgnore
import io.github.kevincianfarini.alchemist.scalar.kiloliters
import io.github.kevincianfarini.alchemist.scalar.milliliters
import io.github.kevincianfarini.alchemist.unit.LengthUnit
import io.github.kevincianfarini.alchemist.unit.VolumeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.Typography.nbsp

class VolumeTest {

    @Test
    @WasmWasiIgnore // See: https://youtrack.jetbrains.com/issue/KT-60964.
    fun to_string_simple() {
        assertEquals(
            expected = "10.00m³",
            actual = 10.kiloliters.toString(),
        )
    }

    @Test
    fun to_string_volume_simple() {
        assertEquals(
            expected = "1.234${nbsp}L",
            actual = 1_234.milliliters.toString(VolumeUnit.Metric.Liter, decimals = 3),
        )
    }

    @Test
    fun to_double_nanometer_works_as_expected() {
        assertEquals(
            expected = 1.234e24,
            actual = 1_234.milliliters.toDouble(LengthUnit.International.Nanometer),
        )
    }
}