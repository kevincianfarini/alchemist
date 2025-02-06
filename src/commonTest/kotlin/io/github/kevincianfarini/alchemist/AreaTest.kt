package io.github.kevincianfarini.alchemist

import io.github.kevincianfarini.alchemist.internal.NEGATIVE_INFINITY
import io.github.kevincianfarini.alchemist.internal.POSITIVE_INFINITY
import io.github.kevincianfarini.alchemist.scalar.kilometers
import io.github.kevincianfarini.alchemist.scalar.liters
import io.github.kevincianfarini.alchemist.scalar.meters
import io.github.kevincianfarini.alchemist.scalar.milliliters
import io.github.kevincianfarini.alchemist.scalar.millimeters
import io.github.kevincianfarini.alchemist.scalar.mm2
import io.github.kevincianfarini.alchemist.scalar.nanometers
import io.github.kevincianfarini.alchemist.unit.LengthUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AreaTest {

    @Test
    fun infinite_area_div_finite_length_is_infinite() {
        assertEquals(POSITIVE_INFINITY.nanometers, POSITIVE_INFINITY.mm2 / 1.nanometers)
        assertEquals(NEGATIVE_INFINITY.nanometers, POSITIVE_INFINITY.mm2 / (-1).nanometers)
        assertEquals(NEGATIVE_INFINITY.nanometers, NEGATIVE_INFINITY.mm2 / 1.nanometers)
        assertEquals(POSITIVE_INFINITY.nanometers, NEGATIVE_INFINITY.mm2 / (-1).nanometers)
    }

    @Test
    fun finite_area_div_infinite_length_is_zero() {
        assertEquals(0.nanometers, (Long.MAX_VALUE - 1).mm2 / POSITIVE_INFINITY.nanometers)
        assertEquals(0.nanometers, (Long.MAX_VALUE - 1).mm2 / NEGATIVE_INFINITY.nanometers)
        assertEquals(0.nanometers, (Long.MIN_VALUE + 1).mm2 / POSITIVE_INFINITY.nanometers)
        assertEquals(0.nanometers, (Long.MIN_VALUE + 1).mm2 / NEGATIVE_INFINITY.nanometers)
    }

    @Test
    fun infinite_area_div_infinite_length_throws() {
        assertFailsWith<IllegalArgumentException> {
            POSITIVE_INFINITY.mm2 / POSITIVE_INFINITY.nanometers
        }
        assertFailsWith<IllegalArgumentException> {
            NEGATIVE_INFINITY.mm2 / POSITIVE_INFINITY.nanometers
        }
        assertFailsWith<IllegalArgumentException> {
            NEGATIVE_INFINITY.mm2 / NEGATIVE_INFINITY.nanometers
        }
        assertFailsWith<IllegalArgumentException> {
            POSITIVE_INFINITY.mm2 / NEGATIVE_INFINITY.nanometers
        }
    }

    @Test
    fun nano2_precision_div_length_is_accurate() {
        assertEquals(123_000_000_000_000.nanometers, 123.mm2 / 1.nanometers)
    }

    @Test
    fun micro2_precision_div_length_is_accurate() {
        assertEquals(9_223_373.millimeters, 9_223_373.mm2 / 1.millimeters)
    }

    @Test
    fun milli2_precision_div_length_is_accurate() {
        assertEquals(1.kilometers, 9_223_373_000_000.mm2 / 9_223_373_000_000.nanometers)
    }

    @Test
    fun area_div_scale_is_correct() {
        assertEquals(1.mm2, 2.mm2 / 2)
        assertEquals(0.mm2, 1.mm2 / 2)
        assertEquals(POSITIVE_INFINITY.mm2, POSITIVE_INFINITY.mm2 / 2)
        assertEquals(NEGATIVE_INFINITY.mm2, POSITIVE_INFINITY.mm2 / -2)
        assertEquals(NEGATIVE_INFINITY.mm2, NEGATIVE_INFINITY.mm2 / 2)
        assertEquals(POSITIVE_INFINITY.mm2, NEGATIVE_INFINITY.mm2 / -2)
    }

    @Test
    fun area_div_area_is_correct() {
        assertEquals(1.0, 1.mm2 / 1.mm2)
        assertEquals(2.5, 5.mm2 / 2.mm2)
        assertEquals(Double.POSITIVE_INFINITY, POSITIVE_INFINITY.mm2 / 2.mm2)
        assertEquals(Double.NEGATIVE_INFINITY, NEGATIVE_INFINITY.mm2 / 2.mm2)
        assertEquals(Double.NEGATIVE_INFINITY, POSITIVE_INFINITY.mm2 / (-2).mm2)
        assertEquals(Double.POSITIVE_INFINITY, NEGATIVE_INFINITY.mm2 / (-2).mm2)
    }

    @Test
    fun area_minus_area_is_correct() {
        assertEquals((-1).mm2, 0.mm2 - 1.mm2)
        assertEquals(5.mm2, 10.mm2 - 5.mm2)
        assertEquals(12.mm2, 10.mm2 - (-2).mm2)
        assertEquals(NEGATIVE_INFINITY.mm2, (Long.MIN_VALUE + 1).mm2 - 1.mm2)
        assertEquals(POSITIVE_INFINITY.mm2, (Long.MAX_VALUE - 1).mm2 - (-1).mm2)
        assertEquals(POSITIVE_INFINITY.mm2, POSITIVE_INFINITY.mm2 - 100_000_000.mm2)
        assertEquals(NEGATIVE_INFINITY.mm2, NEGATIVE_INFINITY.mm2 - 100_000_000.mm2)
    }

    @Test
    fun area_plus_area_is_correct() {
        assertEquals(1.mm2, 0.mm2 + 1.mm2)
        assertEquals(15.mm2, 10.mm2 + 5.mm2)
        assertEquals(8.mm2, 10.mm2 + (-2).mm2)
        assertEquals(NEGATIVE_INFINITY.mm2, (Long.MIN_VALUE + 1).mm2 + (-1).mm2)
        assertEquals(POSITIVE_INFINITY.mm2, (Long.MAX_VALUE - 1).mm2 + 1.mm2)
        assertEquals(POSITIVE_INFINITY.mm2, POSITIVE_INFINITY.mm2 + 100_000_000.mm2)
        assertEquals(NEGATIVE_INFINITY.mm2, NEGATIVE_INFINITY.mm2 + 100_000_000.mm2)
    }

    @Test
    fun area_mul_scale_is_correct() {
        assertEquals(4.mm2, 2.mm2 * 2)
        assertEquals(0.mm2, 0.mm2 * 2)
        assertEquals(POSITIVE_INFINITY.mm2, POSITIVE_INFINITY.mm2 * 2)
        assertEquals(NEGATIVE_INFINITY.mm2, POSITIVE_INFINITY.mm2 * -2)
        assertEquals(NEGATIVE_INFINITY.mm2, NEGATIVE_INFINITY.mm2 * 2)
        assertEquals(POSITIVE_INFINITY.mm2, NEGATIVE_INFINITY.mm2 * -2)
    }

    @Test
    fun area_to_double_is_correct() {
        val scale = 123_456_789_987_654_321
        val area = scale.mm2
        assertEquals(scale.toDouble(), area.toDouble(LengthUnit.International.Millimeter))
        assertEquals(scale.toDouble() / 1_000_000, area.toDouble(LengthUnit.International.Meter))
        assertEquals(scale.toDouble() / 1_000_000_000_000, area.toDouble(LengthUnit.International.Kilometer))
        assertEquals(scale.toDouble() / 1_000_000_000_000_000_000, area.toDouble(LengthUnit.International.Megameter))
    }

    @Test
    fun to_international_components_works() {
        val mega = 1_000_000_000_000_000_000.mm2
        val kilo = 1_000_000_000_000.mm2
        val meters = 1_000_000.mm2
        val centi = 100.mm2
        val milli = 1.mm2
        val area = mega + kilo + meters + centi + milli
        area.toInternationalComponents { megametersSquared, kilometersSquared, metersSquared, centimetersSquared, millimetersSquared ->
            assertEquals(1, megametersSquared)
            assertEquals(1, kilometersSquared)
            assertEquals(1, metersSquared)
            assertEquals(1, centimetersSquared)
            assertEquals(1, millimetersSquared)
        }
    }

    @Test
    fun infinity_to_international_components_works() {
        POSITIVE_INFINITY.mm2.toInternationalComponents { megametersSquared, kilometersSquared, metersSquared, centimetersSquared, millimetersSquared ->
            assertEquals(megametersSquared, Long.MAX_VALUE)
            assertEquals(kilometersSquared, Long.MAX_VALUE)
            assertEquals(metersSquared, Long.MAX_VALUE)
            assertEquals(centimetersSquared, Long.MAX_VALUE)
            assertEquals(millimetersSquared, Long.MAX_VALUE)
        }
        NEGATIVE_INFINITY.mm2.toInternationalComponents { megametersSquared, kilometersSquared, metersSquared, centimetersSquared, millimetersSquared ->
            assertEquals(megametersSquared, Long.MIN_VALUE)
            assertEquals(kilometersSquared, Long.MIN_VALUE)
            assertEquals(metersSquared, Long.MIN_VALUE)
            assertEquals(centimetersSquared, Long.MIN_VALUE)
            assertEquals(millimetersSquared, Long.MIN_VALUE)
        }
    }

    @Test
    @WasmWasiIgnore // See: https://youtrack.jetbrains.com/issue/KT-60964.
    fun to_string_override_works() {
        assertEquals("0.00mm²", 0.mm2.toString())
        assertEquals("1.00mm²", 1.mm2.toString())
        assertEquals("1.00cm²", 100.mm2.toString())
        assertEquals("1.01cm²", 101.mm2.toString())
        assertEquals("1.00m²", 1_000_000.mm2.toString())
        assertEquals("1.01m²", 1_010_000.mm2.toString())
        assertEquals("1.00km²", 1_000_000_000_000.mm2.toString())
        assertEquals("1.01km²", 1_010_000_000_000.mm2.toString())
        assertEquals("1.00Mm²", 1_000_000_000_000_000_000.mm2.toString())
        assertEquals("1.01Mm²", 1_010_000_000_000_000_000.mm2.toString())
        assertEquals("Infinity", POSITIVE_INFINITY.mm2.toString())
        assertEquals("-Infinity", NEGATIVE_INFINITY.mm2.toString())
    }

    @Test
    fun compare_to_works() {
        assertTrue(1.mm2 < 2.mm2)
        assertTrue(2.mm2 <= 2.mm2)
        assertFalse(1.mm2 > 2.mm2)
        assertFalse(1.mm2 >= 2.mm2)
        assertFalse(POSITIVE_INFINITY.mm2 > POSITIVE_INFINITY.mm2)
        assertFalse(POSITIVE_INFINITY.mm2 < POSITIVE_INFINITY.mm2)
        assertTrue(POSITIVE_INFINITY.mm2 >= POSITIVE_INFINITY.mm2)
        assertTrue(POSITIVE_INFINITY.mm2 <= POSITIVE_INFINITY.mm2)
        assertTrue(POSITIVE_INFINITY.mm2 > NEGATIVE_INFINITY.mm2)
        assertTrue(POSITIVE_INFINITY.mm2 >= NEGATIVE_INFINITY.mm2)
        assertFalse(NEGATIVE_INFINITY.mm2 > NEGATIVE_INFINITY.mm2)
        assertFalse(NEGATIVE_INFINITY.mm2 < NEGATIVE_INFINITY.mm2)
        assertTrue(NEGATIVE_INFINITY.mm2 >= NEGATIVE_INFINITY.mm2)
        assertTrue(NEGATIVE_INFINITY.mm2 <= NEGATIVE_INFINITY.mm2)
        assertTrue(NEGATIVE_INFINITY.mm2 < POSITIVE_INFINITY.mm2)
        assertTrue(NEGATIVE_INFINITY.mm2 <= POSITIVE_INFINITY.mm2)
    }

    @Test
    fun is_finite_works() {
        assertTrue(1.mm2.isFinite())
        assertTrue((Long.MAX_VALUE - 1).mm2.isFinite())
        assertTrue((Long.MIN_VALUE + 1).mm2.isFinite())
        assertFalse(Long.MAX_VALUE.mm2.isFinite())
        assertFalse(Long.MIN_VALUE.mm2.isFinite())
        assertFalse(POSITIVE_INFINITY.mm2.isFinite())
        assertFalse(NEGATIVE_INFINITY.mm2.isFinite())
    }

    @Test
    fun area_mul_length_simple() {
        assertEquals(
            expected = 1_219_251.milliliters,
            actual = 123_456_000_000.mm2 * 9_876.nanometers,
        )
    }
}