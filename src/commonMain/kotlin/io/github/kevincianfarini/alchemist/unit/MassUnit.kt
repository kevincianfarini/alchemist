package io.github.kevincianfarini.alchemist.unit

/**
 * A unit of mass precise to the microgram.
 */
public interface MassUnit {

    /**
     * The amount of micrograms in this unit. Implementations of [MassUnit] should be perfectly divisible by a
     * quantity of micrograms.
     */
    public val microgramScale: Long

    /**
     * The symbol of this unit.
     */
    public val symbol: String

    /**
     * An International System of Units standard representation of mass.
     */
    public object International {
        public val Microgram: MassUnit = object : MassUnit {
            override val microgramScale: Long = 1
            override val symbol: String = "μg"
        }
        public val Milligram: MassUnit = object : MassUnit {
            override val microgramScale: Long = 1_000
            override val symbol: String = "mg"
        }
        public val Gram: MassUnit = object : MassUnit {
            override val microgramScale: Long = 1_000_000
            override val symbol: String = "g"
        }
        public val Kilogram: MassUnit = object : MassUnit {
            override val microgramScale: Long = 1_000_000_000
            override val symbol: String = "kg"
        }
        public val Megagram: MassUnit = object : MassUnit {
            override val microgramScale: Long = 1_000_000_000_000
            override val symbol: String = "Mg"
        }
        public val Gigagram: MassUnit = object : MassUnit {
            override val microgramScale: Long = 1_000_000_000_000_000
            override val symbol: String = "Gg"
        }
        public val Teragram: MassUnit = object : MassUnit {
            override val microgramScale: Long = 1_000_000_000_000_000_000
            override val symbol: String = "Tg"
        }
        public val entries: List<MassUnit> = listOf(Microgram, Milligram, Gram, Kilogram, Megagram, Gigagram, Teragram)
    }
}
