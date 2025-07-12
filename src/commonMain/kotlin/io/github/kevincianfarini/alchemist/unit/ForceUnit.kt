package io.github.kevincianfarini.alchemist.unit

/**
 * A unit of force precise to the nanonewton.
 */
public interface ForceUnit {

    /**
     * The symbol of this unit.
     */
    public val symbol: String

    /**
     * The amount of nanonewtons in this unit. Implementations of [ForceUnit] should be perfectly divisible by a
     * quantity of nanonewtons.
     */
    public val nanonewtonScale: Long

    /**
     * An International System of Units standard representation of force.
     */
    public object International {
        public val Nanonewton: ForceUnit = object : ForceUnit {
            override val symbol: String = "nN"
            override val nanonewtonScale: Long = 1
        }
        public val Micronewton: ForceUnit = object : ForceUnit {
            override val symbol: String = "μN"
            override val nanonewtonScale: Long = 1_000
        }
        public val Millinewton: ForceUnit = object : ForceUnit {
            override val symbol: String = "mN"
            override val nanonewtonScale: Long = 1_000_000
        }
        public val Newton: ForceUnit = object : ForceUnit {
            override val symbol: String = "N"
            override val nanonewtonScale: Long = 1_000_000_000
        }
        public val Kilonewton: ForceUnit = object : ForceUnit {
            override val symbol: String = "kN"
            override val nanonewtonScale: Long = 1_000_000_000_000
        }
        public val Meganewton: ForceUnit = object : ForceUnit {
            override val symbol: String = "MN"
            override val nanonewtonScale: Long = 1_000_000_000_000_000
        }
        public val Giganewton: ForceUnit = object : ForceUnit {
            override val symbol: String = "GN"
            override val nanonewtonScale: Long = 1_000_000_000_000_000_000
        }
        public val entries: List<ForceUnit> = listOf(Nanonewton, Micronewton, Millinewton, Newton, Kilonewton, Meganewton, Giganewton)
    }
}
