package io.github.kevincianfarini.alchemist

import io.github.kevincianfarini.alchemist.Energy.Companion.gigajoules
import io.github.kevincianfarini.alchemist.Energy.Companion.gigawattHours
import io.github.kevincianfarini.alchemist.Energy.Companion.joules
import io.github.kevincianfarini.alchemist.Energy.Companion.kilojoules
import io.github.kevincianfarini.alchemist.Energy.Companion.kilowattHours
import io.github.kevincianfarini.alchemist.Energy.Companion.megajoules
import io.github.kevincianfarini.alchemist.Energy.Companion.megawattHours
import io.github.kevincianfarini.alchemist.Energy.Companion.millijoules
import io.github.kevincianfarini.alchemist.Energy.Companion.milliwattHours
import io.github.kevincianfarini.alchemist.Energy.Companion.petajoules
import io.github.kevincianfarini.alchemist.Energy.Companion.terawattHours
import io.github.kevincianfarini.alchemist.Energy.Companion.tetrajoules
import io.github.kevincianfarini.alchemist.Energy.Companion.wattHours
import io.github.kevincianfarini.alchemist.Power.Companion.microwatts
import io.github.kevincianfarini.alchemist.Power.Companion.watts
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class EnergyTest {

    @Test
    fun to_international_components_works() {
        val energy = 1.petajoules + 1.tetrajoules + 1.gigajoules + 1.megajoules + 1.kilojoules + 1.joules + 1.millijoules
        energy.toInternationalComponents { petajoules, tetrajoules, gigajoules, megajoules, kilojoules, joules, millijoules ->
            assertEquals(1L, petajoules)
            assertEquals(1L, tetrajoules)
            assertEquals(1L, gigajoules)
            assertEquals(1L, megajoules)
            assertEquals(1L, kilojoules)
            assertEquals(1L, joules)
            assertEquals(1L, millijoules)
        }
    }

    @Test
    fun infinite_to_international_components() {
        Energy.POSITIVE_INFINITY.toInternationalComponents { petajoules, tetrajoules, gigajoules, megajoules, kilojoules, joules, millijoules ->
            assertEquals(Long.MAX_VALUE, petajoules)
            assertEquals(Long.MAX_VALUE, tetrajoules)
            assertEquals(Long.MAX_VALUE, gigajoules)
            assertEquals(Long.MAX_VALUE, megajoules)
            assertEquals(Long.MAX_VALUE, kilojoules)
            assertEquals(Long.MAX_VALUE, joules)
            assertEquals(Long.MAX_VALUE, millijoules)
        }
    }

    @Test
    fun to_electricity_components_works() {
        val energy = 1.terawattHours + 1.gigawattHours + 1.megawattHours + 1.kilowattHours + 1.wattHours + 1.milliwattHours + 3.millijoules
        energy.toElectricityComponents { terawattHours, gigawattHours, megawattHours, kilowattHours, wattHours, milliwattHours, microwattHours ->
            assertEquals(1L, terawattHours)
            assertEquals(1L, gigawattHours)
            assertEquals(1L, megawattHours)
            assertEquals(1L, kilowattHours)
            assertEquals(1L, wattHours)
            assertEquals(1L, milliwattHours)
            assertEquals(0.8333333333, microwattHours, absoluteTolerance = 0.000001)
        }
    }

    @Test
    fun infinite_to_electricity_components() {
        Energy.POSITIVE_INFINITY.toElectricityComponents { terawattHours, gigawattHours, megawattHours, kilowattHours, wattHours, milliwattHours, microwattHours ->
            assertEquals(Long.MAX_VALUE, terawattHours)
            assertEquals(Long.MAX_VALUE, gigawattHours)
            assertEquals(Long.MAX_VALUE, megawattHours)
            assertEquals(Long.MAX_VALUE, kilowattHours)
            assertEquals(Long.MAX_VALUE, wattHours)
            assertEquals(Long.MAX_VALUE, milliwattHours)
            assertEquals(Double.POSITIVE_INFINITY, microwattHours)
        }
    }

    @Test
    fun to_double_works() {
        val energy = 12_345.wattHours
        assertEquals(12.345, energy.toDouble(EnergyUnit.Electricity.KilowattHour))
    }

    @Test
    fun to_decial_string_works() {
        val energy = 12_345.wattHours
        assertEquals("12.345kWh", energy.toString(EnergyUnit.Electricity.KilowattHour))
    }

    @Test
    fun to_string_picks_correct_energy_display_unit() {
        val energy = 12_345.joules
        assertEquals("12.345kJ", energy.toString())
    }

    @Test
    fun infinite_energy_div_infinite_duration_throws() {
        assertFailsWith<IllegalArgumentException> {
            Energy.POSITIVE_INFINITY / Duration.INFINITE
        }
    }

    @Test
    fun infinite_energy_div_finite_duration_produces_infinite_power() {
        assertEquals(Power.POSITIVE_INFINITY, Energy.POSITIVE_INFINITY / 10.hours)
        assertEquals(Power.NEGATIVE_INFINITY, Energy.NEGATIVE_INFINITY / 10.hours)
    }

    @Test
    fun finite_energy_div_infinite_duration_produces_zero_power() {
        assertEquals(0.watts, 1.petajoules / Duration.INFINITE)
    }

    @Test
    fun pico_granular_energy_div_micro_granular_time_power_works() {
        assertEquals(1.watts, 1.joules / 1.seconds)
    }

    @Test
    fun nano_granular_energy_div_micro_granular_time_power_works() {
        assertEquals(1_000_000_000.watts, 1.gigajoules / 1.seconds)
    }

    @Test
    fun micro_granular_energy_div_micro_granular_time_power_works() {
        assertEquals(1_000_000_000_000.watts, 1.tetrajoules / 1.seconds)
    }

    @Test
    fun milli_granular_energy_div_micro_granular_time_power_works() {
        assertEquals(1_000_000_000_000_000.watts, 1.petajoules / 1.seconds)
    }

    @Test
    fun one_microwatt_with_micro_granular_time() {
        assertEquals(1.microwatts, 1.millijoules / 1_000.seconds)
    }

    @Test
    fun one_microwatt_with_milli_granular_time() {
        assertEquals(1.microwatts, 1_000_000_000_000.millijoules / 1_000_000_000_000_000.seconds)
    }
}