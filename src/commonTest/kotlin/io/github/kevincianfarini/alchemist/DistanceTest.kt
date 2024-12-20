package io.github.kevincianfarini.alchemist

import kotlin.test.Test
import kotlin.test.assertEquals

class DistanceTest {
    
    @Test
    fun to_metric_components_works() {
        val distance = 1.gigameters + 1.megameters + 1.kilometers + 1.meters + 1.centimeters + 1.millimeters + 1.micrometers + 1.nanometers
        distance.toInternationalComponents { gm, mega, km, m, cm, milli, um, nm ->
            assertEquals(1L, gm)
            assertEquals(1L, mega)
            assertEquals(1L, km)
            assertEquals(1L, m)
            assertEquals(1L, cm)
            assertEquals(1L, milli)
            assertEquals(1L, um)
            assertEquals(1L, nm)
        }
    }

    @Test
    fun to_metric_components_positive_infinity_max_value_every_component() {
        Distance.POSITIVE_INFINITY.toInternationalComponents { gm, mega, km, m, cm, milli, um, nm ->
            assertEquals(Long.MAX_VALUE, gm)
            assertEquals(Long.MAX_VALUE, mega)
            assertEquals(Long.MAX_VALUE, km)
            assertEquals(Long.MAX_VALUE, m)
            assertEquals(Long.MAX_VALUE, cm)
            assertEquals(Long.MAX_VALUE, milli)
            assertEquals(Long.MAX_VALUE, um)
            assertEquals(Long.MAX_VALUE, nm)
        }
    }

    @Test
    fun to_metric_components_negative_infinity_min_value_every_component() {
        Distance.NEGATIVE_INFINITY.toInternationalComponents { gm, mega, km, m, cm, milli, um, nm ->
            assertEquals(Long.MIN_VALUE, gm)
            assertEquals(Long.MIN_VALUE, mega)
            assertEquals(Long.MIN_VALUE, km)
            assertEquals(Long.MIN_VALUE, m)
            assertEquals(Long.MIN_VALUE, cm)
            assertEquals(Long.MIN_VALUE, milli)
            assertEquals(Long.MIN_VALUE, um)
            assertEquals(Long.MIN_VALUE, nm)
        }
    }

    @Test
    fun to_imperial_components_works() {
        val distance = 1.miles + 1.yards + 1.feet + 1.inches
        distance.toUnitedStatesCustomaryComponents { miles, yards, feet, inches ->
            assertEquals(1L, miles)
            assertEquals(1L, yards)
            assertEquals(1L, feet)
            assertEquals(1.0, inches)
        }
    }

    @Test
    fun to_imperial_components_positive_infinity_max_value_every_component() {
        Distance.POSITIVE_INFINITY.toUnitedStatesCustomaryComponents { miles, yards, feet, inches ->
            assertEquals(Long.MAX_VALUE, miles)
            assertEquals(Long.MAX_VALUE, yards)
            assertEquals(Long.MAX_VALUE, feet)
            assertEquals(Double.POSITIVE_INFINITY, inches)
        }
    }

    @Test
    fun to_imperial_components_negative_infinity_min_value_every_component() {
        Distance.NEGATIVE_INFINITY.toUnitedStatesCustomaryComponents { miles, yards, feet, inches ->
            assertEquals(Long.MIN_VALUE, miles)
            assertEquals(Long.MIN_VALUE, yards)
            assertEquals(Long.MIN_VALUE, feet)
            assertEquals(Double.NEGATIVE_INFINITY, inches)
        }
    }

    @Test
    fun default_to_string_renders_into_metric_components() {
        val distance = 10_000.meters
        assertEquals(
            expected = "10.0km",
            actual = distance.toString(),
        )
    }

    @Test
    fun to_string_infinity() {
        assertEquals("Infinity", Distance.POSITIVE_INFINITY.toString())
        assertEquals("-Infinity", Distance.NEGATIVE_INFINITY.toString())
    }

    @Test
    fun to_double_converts_miles_to_km_correctly() {
        assertEquals(1.609344, 1.miles.toDouble(DistanceUnit.International.Kilometer))
    }

    @Test
    fun distance_mul_distance_maximum_nanometers_component_is_lost_to_precision() {
        // 0.998001998 millimeters², but we lose precision.
        val nanos = 999_999.nanometers
        assertEquals(0.mm2, nanos * nanos)
    }

    @Test
    fun distance_mul_distance_gigameters_is_infinite() {
        assertEquals(Area.POSITIVE_INFINITY, 1.gigameters * 1.gigameters)
        assertEquals(Area.NEGATIVE_INFINITY, 1.gigameters * (-1).gigameters)
        assertEquals(Area.POSITIVE_INFINITY, (-1).gigameters * (-1).gigameters)
        assertEquals(Area.NEGATIVE_INFINITY, (-1).gigameters * 1.gigameters)
    }

    @Test
    fun distance_mul_distance_megameter_overflow() {
        assertEquals(Area.POSITIVE_INFINITY, 100.megameters * 10.megameters)
    }

    @Test
    fun distance_mul_distance_megameter_no_overflow() {
        assertEquals(
            expected = 8_000_000_000_000_000_000L.mm2,
            actual = 4.megameters * 2.megameters
        )
    }

    @Test
    fun distance_squared_is_correct() {
        assertEquals(
            expected = 1_000_000.mm2,
            actual = 1.meters.squared(),
        )
    }
}