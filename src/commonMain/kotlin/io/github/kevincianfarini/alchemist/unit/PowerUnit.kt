package io.github.kevincianfarini.alchemist.unit

/**
 * A unit of power precise to the microwatt.
 */
public interface PowerUnit {

    /**
     * The amount of microwatts in this unit. Implementations of [PowerUnit] should be perfectly divisible by a
     * quantity of microwatts.
     */
    public val microwattScale: Long

    /**
     * The symbol of this unit.
     */
    public val symbol: String

    /**
     * An International System of Units standard representation of power.
     */
    public object International {
        public val Microwatt: PowerUnit = object : PowerUnit {
            override val microwattScale: Long = 1
            override val symbol: String = "μW"
        }
        public val Milliwatt: PowerUnit = object : PowerUnit {
            override val microwattScale: Long = 1_000
            override val symbol: String = "mW"
        }
        public val Watt: PowerUnit = object : PowerUnit {
            override val microwattScale: Long = 1_000_000
            override val symbol: String = "W"
        }
        public val Kilowatt: PowerUnit = object : PowerUnit {
            override val microwattScale: Long = 1_000_000_000
            override val symbol: String = "kW"
        }
        public val Megawatt: PowerUnit = object : PowerUnit {
            override val microwattScale: Long = 1_000_000_000_000
            override val symbol: String = "MW"
        }
        public val Gigawatt: PowerUnit = object : PowerUnit {
            override val microwattScale: Long = 1_000_000_000_000_000
            override val symbol: String = "GW"
        }
        public val Terawatt: PowerUnit = object : PowerUnit {
            override val microwattScale: Long = 1_000_000_000_000_000_000
            override val symbol: String = "TW"
        }
        public val entries: List<PowerUnit> = listOf(Microwatt, Milliwatt, Watt, Kilowatt, Megawatt, Gigawatt, Terawatt)
    }
}
