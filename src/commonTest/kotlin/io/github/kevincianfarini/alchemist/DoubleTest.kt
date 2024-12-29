package io.github.kevincianfarini.alchemist

import kotlin.test.assertEquals
import kotlin.test.Test

class DoubleTest {

    @Test
    fun exact_omits_decimal_point() {
        assertEquals(
            expected = "0",
            actual = 0.0.toDecimalString(0),
        )
    }

    @Test
    fun decimals_rounded_up() {
        assertEquals(
            expected = "123.457",
            actual = 123.4566.toDecimalString(3),
        )
    }

    @Test
    fun pads_with_zeros() {
        assertEquals(
            expected = "123.4565000000",
            actual = 123.4565.toDecimalString(10),
        )
    }

    @Test
    fun negative_sign() {
        assertEquals(
            expected = "-123.457",
            actual = (-123.4566).toDecimalString(3)
        )
    }
}