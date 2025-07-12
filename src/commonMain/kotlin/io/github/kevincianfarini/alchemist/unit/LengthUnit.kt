package io.github.kevincianfarini.alchemist.unit

/**
 * A unit of length precise to the nanometer.
 */
public interface LengthUnit {

    /**
     * The amount of nanometers in this unit. Implementations of [LengthUnit] should be perfectly divisible by a
     * quantity of nanometers.
     */
    public val nanometerScale: Long

    /**
     * The symbol of this unit.
     */
    public val symbol: String

    /**
     * An International System of Units standard representation of length.
     */
    public object International {
        public val Nanometer: LengthUnit = object : LengthUnit {
            override val nanometerScale: Long = 1
            override val symbol: String = "nm"
        }
        public val Micrometer: LengthUnit = object : LengthUnit {
            override val nanometerScale: Long = 1_000
            override val symbol: String = "μm"
        }
        public val Millimeter: LengthUnit = object : LengthUnit {
            override val nanometerScale: Long = 1_000_000
            override val symbol: String = "mm"
        }
        public val Centimeter: LengthUnit = object : LengthUnit {
            override val nanometerScale: Long = 10_000_000
            override val symbol: String = "cm"
        }
        public val Meter: LengthUnit = object : LengthUnit {
            override val nanometerScale: Long = 1_000_000_000
            override val symbol: String = "m"
        }
        public val Kilometer: LengthUnit = object : LengthUnit {
            override val nanometerScale: Long = 1_000_000_000_000
            override val symbol: String = "km"
        }
        public val Megameter: LengthUnit = object : LengthUnit {
            override val nanometerScale: Long = 1_000_000_000_000_000
            override val symbol: String = "Mm"
        }
        public val Gigameter: LengthUnit = object : LengthUnit {
            override val nanometerScale: Long = 1_000_000_000_000_000_000
            override val symbol: String = "Gm"
        }
        public val entries: List<LengthUnit> = listOf(Nanometer, Micrometer, Millimeter, Centimeter, Meter, Kilometer, Megameter, Gigameter)
    }

    /**
     * A non-standard representation of length commonly used in the United States.
     */
    public object UnitedStatesCustomary {
        public val Inch: LengthUnit = object : LengthUnit {
            override val nanometerScale: Long = 25_400_000
            override val symbol: String = "in"
        }
        public val Foot: LengthUnit = object : LengthUnit {
            override val nanometerScale: Long = 304_800_000
            override val symbol: String = "ft"
        }
        public val Yard: LengthUnit = object : LengthUnit {
            override val nanometerScale: Long = 914_400_000
            override val symbol: String = "yd"
        }
        public val Mile: LengthUnit = object : LengthUnit {
            override val nanometerScale: Long = 1_609_344_000_000
            override val symbol: String = "mi"
        }
    }
}
