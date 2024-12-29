package io.github.kevincianfarini.alchemist

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.text.Typography.nbsp

class TemperatureTest {

    @Test
    fun well_known_temperatures_are_precise() {
        assertEquals(0.celsius, 32.fahrenheit)
        assertEquals(100.celsius, 212.fahrenheit)
        assertEquals((-40).celsius, (-40).fahrenheit)
        assertEquals(255_372_222_222.nanokelvins, 0.fahrenheit)
        assertEquals(273_150_000_000.nanokelvins, 0.celsius)
    }

    @Test
    fun to_string_with_unit_works_properly() {
        assertEquals("0.00°F", 0.fahrenheit.toString(TemperatureUnit.Fahrenheit, decimals = 2))
        assertEquals("0.00°C", 0.celsius.toString(TemperatureUnit.International.Celsius, decimals = 2))
        assertEquals("273.85°C", 547.kelvins.toString(TemperatureUnit.International.Celsius, decimals = 2))
        assertEquals("0.001${nbsp}μK", 1.nanokelvins.toString(TemperatureUnit.International.Microkelvin, decimals = 3))
    }

    @Test
    fun to_string_without_unit_picks_proper_unit() {
        assertEquals("0.00${nbsp}nK", 0.nanokelvins.toString())
        assertEquals("1.00${nbsp}nK", 1.nanokelvins.toString())
        assertEquals("1.00${nbsp}μK", 1_000.nanokelvins.toString())
        assertEquals("1.00${nbsp}mK", 1_000_000.nanokelvins.toString())
        assertEquals("1.00${nbsp}K", 1_000_000_000.nanokelvins.toString())
        assertEquals("1.00${nbsp}kK", 1_000_000_000_000.nanokelvins.toString())
        assertEquals("1.00${nbsp}MK", 1_000_000_000_000_000.nanokelvins.toString())
        assertEquals("1.00${nbsp}GK", 1_000_000_000_000_000_000.nanokelvins.toString())
        assertEquals("255.37${nbsp}K", 0.fahrenheit.toString())
        assertEquals("273.15${nbsp}K", 0.celsius.toString())
    }
}