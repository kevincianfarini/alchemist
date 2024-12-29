package io.github.kevincianfarini.alchemist

import kotlin.test.*

class AreaTest {

    @Test
    fun infinite_area_div_finite_length_is_infinite() {
        assertEquals(Length.POSITIVE_INFINITY, Area.POSITIVE_INFINITY / 1.nanometers)
        assertEquals(Length.NEGATIVE_INFINITY, Area.POSITIVE_INFINITY / (-1).nanometers)
        assertEquals(Length.NEGATIVE_INFINITY, Area.NEGATIVE_INFINITY / 1.nanometers)
        assertEquals(Length.POSITIVE_INFINITY, Area.NEGATIVE_INFINITY / (-1).nanometers)
    }

    @Test
    fun finite_area_div_infinite_length_is_zero() {
        assertEquals(0.nanometers, (Long.MAX_VALUE - 1).mm2 / Length.POSITIVE_INFINITY)
        assertEquals(0.nanometers, (Long.MAX_VALUE - 1).mm2 / Length.NEGATIVE_INFINITY)
        assertEquals(0.nanometers, (Long.MIN_VALUE + 1).mm2 / Length.POSITIVE_INFINITY)
        assertEquals(0.nanometers, (Long.MIN_VALUE + 1).mm2 / Length.NEGATIVE_INFINITY)
    }

    @Test
    fun infinite_area_div_infinite_length_throws() {
        assertFailsWith<IllegalArgumentException> {
            Area.POSITIVE_INFINITY / Length.POSITIVE_INFINITY
        }
        assertFailsWith<IllegalArgumentException> {
            Area.NEGATIVE_INFINITY / Length.POSITIVE_INFINITY
        }
        assertFailsWith<IllegalArgumentException> {
            Area.NEGATIVE_INFINITY / Length.NEGATIVE_INFINITY
        }
        assertFailsWith<IllegalArgumentException> {
            Area.POSITIVE_INFINITY / Length.NEGATIVE_INFINITY
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
        assertEquals(Area.POSITIVE_INFINITY, Area.POSITIVE_INFINITY / 2)
        assertEquals(Area.NEGATIVE_INFINITY, Area.POSITIVE_INFINITY / -2)
        assertEquals(Area.NEGATIVE_INFINITY, Area.NEGATIVE_INFINITY / 2)
        assertEquals(Area.POSITIVE_INFINITY, Area.NEGATIVE_INFINITY / -2)
    }

    @Test
    fun area_div_area_is_correct() {
        assertEquals(1.0, 1.mm2 / 1.mm2)
        assertEquals(2.5, 5.mm2 / 2.mm2)
        assertEquals(Double.POSITIVE_INFINITY, Area.POSITIVE_INFINITY / 2.mm2)
        assertEquals(Double.NEGATIVE_INFINITY, Area.NEGATIVE_INFINITY / 2.mm2)
        assertEquals(Double.NEGATIVE_INFINITY, Area.POSITIVE_INFINITY / (-2).mm2)
        assertEquals(Double.POSITIVE_INFINITY, Area.NEGATIVE_INFINITY / (-2).mm2)
    }

    @Test
    fun area_minus_area_is_correct() {
        assertEquals((-1).mm2, 0.mm2 - 1.mm2)
        assertEquals(5.mm2, 10.mm2 - 5.mm2)
        assertEquals(12.mm2, 10.mm2 - (-2).mm2)
        assertEquals(Area.NEGATIVE_INFINITY, (Long.MIN_VALUE + 1).mm2 - 1.mm2)
        assertEquals(Area.POSITIVE_INFINITY, (Long.MAX_VALUE - 1).mm2 - (-1).mm2)
        assertEquals(Area.POSITIVE_INFINITY, Area.POSITIVE_INFINITY - 100_000_000.mm2)
        assertEquals(Area.NEGATIVE_INFINITY, Area.NEGATIVE_INFINITY - 100_000_000.mm2)
    }

    @Test
    fun area_plus_area_is_correct() {
        assertEquals(1.mm2, 0.mm2 + 1.mm2)
        assertEquals(15.mm2, 10.mm2 + 5.mm2)
        assertEquals(8.mm2, 10.mm2 + (-2).mm2)
        assertEquals(Area.NEGATIVE_INFINITY, (Long.MIN_VALUE + 1).mm2 + (-1).mm2)
        assertEquals(Area.POSITIVE_INFINITY, (Long.MAX_VALUE - 1).mm2 + 1.mm2)
        assertEquals(Area.POSITIVE_INFINITY, Area.POSITIVE_INFINITY + 100_000_000.mm2)
        assertEquals(Area.NEGATIVE_INFINITY, Area.NEGATIVE_INFINITY + 100_000_000.mm2)
    }

    @Test
    fun area_mul_scale_is_correct() {
        assertEquals(4.mm2, 2.mm2 * 2)
        assertEquals(0.mm2, 0.mm2 * 2)
        assertEquals(Area.POSITIVE_INFINITY, Area.POSITIVE_INFINITY * 2)
        assertEquals(Area.NEGATIVE_INFINITY, Area.POSITIVE_INFINITY * -2)
        assertEquals(Area.NEGATIVE_INFINITY, Area.NEGATIVE_INFINITY * 2)
        assertEquals(Area.POSITIVE_INFINITY, Area.NEGATIVE_INFINITY * -2)
    }

    @Test
    fun area_to_double_is_correct() {
        val scale = 123_456_789_987_654_321
        val area = scale.mm2
        assertEquals(scale.toDouble(), area.toDouble(AreaUnit.International.MillimeterSquared))
        assertEquals(scale.toDouble() / 1_000_000, area.toDouble(AreaUnit.International.MeterSquared))
        assertEquals(scale.toDouble() / 1_000_000_000_000, area.toDouble(AreaUnit.International.KilometerSquared))
        assertEquals(scale.toDouble() / 1_000_000_000_000_000_000, area.toDouble(AreaUnit.International.MegameterSquared))
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
        Area.POSITIVE_INFINITY.toInternationalComponents { megametersSquared, kilometersSquared, metersSquared, centimetersSquared, millimetersSquared ->
            assertEquals(megametersSquared, Long.MAX_VALUE)
            assertEquals(kilometersSquared, Long.MAX_VALUE)
            assertEquals(metersSquared, Long.MAX_VALUE)
            assertEquals(centimetersSquared, Long.MAX_VALUE)
            assertEquals(millimetersSquared, Long.MAX_VALUE)
        }
        Area.NEGATIVE_INFINITY.toInternationalComponents { megametersSquared, kilometersSquared, metersSquared, centimetersSquared, millimetersSquared ->
            assertEquals(megametersSquared, Long.MIN_VALUE)
            assertEquals(kilometersSquared, Long.MIN_VALUE)
            assertEquals(metersSquared, Long.MIN_VALUE)
            assertEquals(centimetersSquared, Long.MIN_VALUE)
            assertEquals(millimetersSquared, Long.MIN_VALUE)
        }
    }

    @Test
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
        assertEquals("Infinity", Area.POSITIVE_INFINITY.toString())
        assertEquals("-Infinity", Area.NEGATIVE_INFINITY.toString())
    }

    @Test
    fun compare_to_works() {
        assertTrue(1.mm2 < 2.mm2)
        assertTrue(2.mm2 <= 2.mm2)
        assertFalse(1.mm2 > 2.mm2)
        assertFalse(1.mm2 >= 2.mm2)
        assertFalse(Area.POSITIVE_INFINITY > Area.POSITIVE_INFINITY)
        assertFalse(Area.POSITIVE_INFINITY < Area.POSITIVE_INFINITY)
        assertTrue(Area.POSITIVE_INFINITY >= Area.POSITIVE_INFINITY)
        assertTrue(Area.POSITIVE_INFINITY <= Area.POSITIVE_INFINITY)
        assertTrue(Area.POSITIVE_INFINITY > Area.NEGATIVE_INFINITY)
        assertTrue(Area.POSITIVE_INFINITY >= Area.NEGATIVE_INFINITY)
        assertFalse(Area.NEGATIVE_INFINITY > Area.NEGATIVE_INFINITY)
        assertFalse(Area.NEGATIVE_INFINITY < Area.NEGATIVE_INFINITY)
        assertTrue(Area.NEGATIVE_INFINITY >= Area.NEGATIVE_INFINITY)
        assertTrue(Area.NEGATIVE_INFINITY <= Area.NEGATIVE_INFINITY)
        assertTrue(Area.NEGATIVE_INFINITY < Area.POSITIVE_INFINITY)
        assertTrue(Area.NEGATIVE_INFINITY <= Area.POSITIVE_INFINITY)
    }

    @Test
    fun is_finite_works() {
        assertTrue(1.mm2.isFinite())
        assertTrue((Long.MAX_VALUE - 1).mm2.isFinite())
        assertTrue((Long.MIN_VALUE + 1).mm2.isFinite())
        assertFalse(Long.MAX_VALUE.mm2.isFinite())
        assertFalse(Long.MIN_VALUE.mm2.isFinite())
        assertFalse(Area.POSITIVE_INFINITY.isFinite())
        assertFalse(Area.NEGATIVE_INFINITY.isFinite())
    }
}