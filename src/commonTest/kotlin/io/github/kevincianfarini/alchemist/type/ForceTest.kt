package io.github.kevincianfarini.alchemist.type

import io.github.kevincianfarini.alchemist.scalar.millijoules
import io.github.kevincianfarini.alchemist.scalar.nanometers
import io.github.kevincianfarini.alchemist.scalar.nanonewtons
import kotlin.test.Test
import kotlin.test.assertEquals

class ForceTest {

    @Test
    fun force_mul_length_energy_simple() {
        assertEquals(
            expected = 121.millijoules,
            actual = 123_456_789.nanonewtons * 987_654_321.nanometers,
        )
    }
}