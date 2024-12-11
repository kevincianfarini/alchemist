package io.github.kevincianfarini.alchemist

import io.github.kevincianfarini.alchemist.Distance.Companion.centimeters
import io.github.kevincianfarini.alchemist.Distance.Companion.feet
import io.github.kevincianfarini.alchemist.Distance.Companion.gigameters
import io.github.kevincianfarini.alchemist.Distance.Companion.inches
import io.github.kevincianfarini.alchemist.Distance.Companion.kilometers
import io.github.kevincianfarini.alchemist.Distance.Companion.megameters
import io.github.kevincianfarini.alchemist.Distance.Companion.meters
import io.github.kevincianfarini.alchemist.Distance.Companion.micrometers
import io.github.kevincianfarini.alchemist.Distance.Companion.miles
import io.github.kevincianfarini.alchemist.Distance.Companion.millimeters
import io.github.kevincianfarini.alchemist.Distance.Companion.nanometers
import io.github.kevincianfarini.alchemist.Distance.Companion.yards
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
    fun default_to_string_renders_into_metric_components() {
        val distance = 10_000.meters
        assertEquals(
            expected = "10.0km",
            actual = distance.toString(),
        )
    }

    @Test
    fun to_double_converts_miles_to_km_correctly() {
        assertEquals(1.609344, 1.miles.toDouble(DistanceUnit.International.Kilometer))
    }
}