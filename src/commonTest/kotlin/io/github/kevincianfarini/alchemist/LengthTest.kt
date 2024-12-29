package io.github.kevincianfarini.alchemist

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.seconds

class LengthTest {
    
    @Test
    fun to_metric_components_works() {
        val length = 1.gigameters + 1.megameters + 1.kilometers + 1.meters + 1.centimeters + 1.millimeters + 1.micrometers + 1.nanometers
        length.toInternationalComponents { gm, mega, km, m, cm, milli, um, nm ->
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
        Length.POSITIVE_INFINITY.toInternationalComponents { gm, mega, km, m, cm, milli, um, nm ->
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
        Length.NEGATIVE_INFINITY.toInternationalComponents { gm, mega, km, m, cm, milli, um, nm ->
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
        val length = 1.miles + 1.yards + 1.feet + 1.inches
        length.toUnitedStatesCustomaryComponents { miles, yards, feet, inches ->
            assertEquals(1L, miles)
            assertEquals(1L, yards)
            assertEquals(1L, feet)
            assertEquals(1.0, inches)
        }
    }

    @Test
    fun to_imperial_components_positive_infinity_max_value_every_component() {
        Length.POSITIVE_INFINITY.toUnitedStatesCustomaryComponents { miles, yards, feet, inches ->
            assertEquals(Long.MAX_VALUE, miles)
            assertEquals(Long.MAX_VALUE, yards)
            assertEquals(Long.MAX_VALUE, feet)
            assertEquals(Double.POSITIVE_INFINITY, inches)
        }
    }

    @Test
    fun to_imperial_components_negative_infinity_min_value_every_component() {
        Length.NEGATIVE_INFINITY.toUnitedStatesCustomaryComponents { miles, yards, feet, inches ->
            assertEquals(Long.MIN_VALUE, miles)
            assertEquals(Long.MIN_VALUE, yards)
            assertEquals(Long.MIN_VALUE, feet)
            assertEquals(Double.NEGATIVE_INFINITY, inches)
        }
    }

    @Test
    fun default_to_string_renders_into_metric_components() {
        val length = 10_000.meters
        assertEquals(
            expected = "10.00km",
            actual = length.toString(),
        )
    }

    @Test
    fun to_string_infinity() {
        assertEquals("Infinity", Length.POSITIVE_INFINITY.toString())
        assertEquals("-Infinity", Length.NEGATIVE_INFINITY.toString())
    }

    @Test
    fun to_double_converts_miles_to_km_correctly() {
        assertEquals(1.609344, 1.miles.toDouble(LengthUnit.International.Kilometer))
    }

    @Test
    fun length_mul_length_maximum_nanometers_component_is_lost_to_precision() {
        // 0.998001998 millimeters², but we lose precision.
        val nanos = 999_999.nanometers
        assertEquals(0.mm2, nanos * nanos)
    }

    @Test
    fun length_mul_length_gigameters_is_infinite() {
        assertEquals(Area.POSITIVE_INFINITY, 1.gigameters * 1.gigameters)
        assertEquals(Area.NEGATIVE_INFINITY, 1.gigameters * (-1).gigameters)
        assertEquals(Area.POSITIVE_INFINITY, (-1).gigameters * (-1).gigameters)
        assertEquals(Area.NEGATIVE_INFINITY, (-1).gigameters * 1.gigameters)
    }

    @Test
    fun length_mul_length_megameter_overflow() {
        assertEquals(Area.POSITIVE_INFINITY, 100.megameters * 10.megameters)
    }

    @Test
    fun length_mul_length_megameter_no_overflow() {
        assertEquals(
            expected = 8_000_000_000_000_000_000L.mm2,
            actual = 4.megameters * 2.megameters
        )
    }

    @Test
    fun length_squared_is_correct() {
        assertEquals(
            expected = 1_000_000.mm2,
            actual = 1.meters.squared(),
        )
    }

    @Test
    fun infinite_length_div_infinite_time_throws() {
        assertFailsWith<IllegalArgumentException> {
            Length.POSITIVE_INFINITY / Duration.INFINITE
        }
        assertFailsWith<IllegalArgumentException> {
            Length.NEGATIVE_INFINITY / Duration.INFINITE
        }
        assertFailsWith<IllegalArgumentException> {
            Length.POSITIVE_INFINITY / -Duration.INFINITE
        }
        assertFailsWith<IllegalArgumentException> {
            Length.NEGATIVE_INFINITY / -Duration.INFINITE
        }
    }

    @Test
    fun infinite_length_div_finite_time() {
        assertEquals(Velocity.POSITIVE_INFINITY, Length.POSITIVE_INFINITY / 1.seconds)
        assertEquals(Velocity.NEGATIVE_INFINITY, Length.NEGATIVE_INFINITY / 1.seconds)
        assertEquals(Velocity.NEGATIVE_INFINITY, Length.POSITIVE_INFINITY / (-1).seconds)
        assertEquals(Velocity.POSITIVE_INFINITY, Length.NEGATIVE_INFINITY / (-1).seconds)
    }

    @Test
    fun finite_length_div_infinite_time() {
        assertEquals(0.nmPerSecond, (Long.MAX_VALUE - 1).nanometers / Duration.INFINITE)
        assertEquals(0.nmPerSecond, (Long.MAX_VALUE - 1).nanometers / -Duration.INFINITE)
    }

    @Test
    fun length_div_time_attometer_nanosecond_precision() {
        // 124,999,998.8609375 nm/s, but we lose precision.
        assertEquals(
            expected = 124_999_998.nmPerSecond,
            actual = 123_456_789.nanometers / 987_654_321.nanoseconds,
        )
    }

    @Test
    fun length_div_time_femtometer_nanosecond_precision() {
        // 124,999,998,860.9375 nm/s, but we lose precision.
        assertEquals(
            expected = 124_999_998_860.nmPerSecond,
            actual = 123_456_789.micrometers / 987_654_321.nanoseconds,
        )
    }

    @Test
    fun length_div_time_picometer_nanosecond_precision() {
        // 124,999,998,860,937.500014 nm/s, but we lose precision.
        assertEquals(
            expected = 124_999_998_860_937.nmPerSecond,
            actual = 123_456_789.millimeters / 987_654_321.nanoseconds,
        )
    }

    @Test
    fun length_div_time_nanometer_nanosecond_precision() {
        // 124,999,998,860,937,500.014238 nm/s, but we lose precision.
        assertEquals(
            expected = 124_999_998_860_937_500.nmPerSecond,
            actual = 123_456_789.meters / 987_654_321.nanoseconds,
        )
    }

    @Test
    fun length_div_time_picometer_millisecond_precision() {
        // 124.999998860937500014 nm/s, but we lose precision.
        assertEquals(
            expected = 124.nmPerSecond,
            actual = 123_456_789.millimeters / 987_654_321_000.seconds,
        )
    }

    @Test
    fun length_div_time_nanometer_millisecond_precision() {
        // 124,999.9988609375 nm/s, but we lose precision.
        assertEquals(
            expected = 124_999.nmPerSecond,
            actual = 123_456_789.meters / 987_654_321_000.seconds,
        )
    }

    @Test
    fun length_div_time_defaults_to_millisecond_precision_if_all_nanosecond_are_infinite() {
        // 124,999.998860937500014 nm/s, but we lose precision.
        assertEquals(
            expected = 124_999.nmPerSecond,
            actual = 123_456_789.millimeters / 987_654_321.seconds,
        )
    }

    @Test
    fun length_div_time_defaults_to_coarse_nanometers_per_second_when_picometer_remainder_is_infinite() {
        // 1,000 nm/s, but we lose precision.
        assertEquals(
            expected = 0.nmPerSecond,
            actual = ((Long.MAX_VALUE / 2) - 2).nanometers / ((Long.MAX_VALUE / 2) - 1).milliseconds
        )
    }

    @Test
    fun length_div_velocity_simple() {
        val length = 10.meters
        val velocity = 3.meters / 2.seconds
        assertEquals(6_666_666_666.nanoseconds, length / velocity)
    }
}