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
import kotlin.test.Test
import kotlin.test.assertEquals

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
    fun to_electricity_components_works() {
        val energy = 1.terawattHours + 1.gigawattHours + 1.megawattHours + 1.kilowattHours + 1.wattHours + 1.milliwattHours
        energy.toElectricityComponents { terawattHours, gigawattHours, megawattHours, kilowattHours, wattHours, milliwattHours ->
            assertEquals(1L, terawattHours)
            assertEquals(1L, gigawattHours)
            assertEquals(1L, megawattHours)
            assertEquals(1L, kilowattHours)
            assertEquals(1L, wattHours)
            assertEquals(1L, milliwattHours)
        }
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
}