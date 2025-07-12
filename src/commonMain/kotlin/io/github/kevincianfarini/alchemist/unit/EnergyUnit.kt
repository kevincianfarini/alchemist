package io.github.kevincianfarini.alchemist.unit

/**
 * A unit of energy precise to the millijoule.
 */
public interface EnergyUnit {

    /**
     * The amount of millijoules in this unit. Implementations of [EnergyUnit] should be perfectly divisible by a
     * quantity of millijoules.
     */
    public val millijouleScale: Long

    /**
     * The symbol of this unit.
     */
    public val symbol: String

    /**
     * An International System of Units standard representation of energy.
     */
    public object International {
        public val Millijoule: EnergyUnit = object : EnergyUnit {
            override val millijouleScale: Long = 1
            override val symbol: String = "mJ"
        }
        public val Joule: EnergyUnit = object : EnergyUnit {
            override val millijouleScale: Long = 1_000
            override val symbol: String = "J"
        }
        public val Kilojoule: EnergyUnit = object : EnergyUnit {
            override val millijouleScale: Long = 1_000_000
            override val symbol: String = "kJ"
        }
        public val Megajoule: EnergyUnit = object : EnergyUnit {
            override val millijouleScale: Long = 1_000_000_000
            override val symbol: String = "MJ"
        }
        public val Gigajoule: EnergyUnit = object : EnergyUnit {
            override val millijouleScale: Long = 1_000_000_000_000
            override val symbol: String = "GJ"
        }
        public val Tetrajoule: EnergyUnit = object : EnergyUnit {
            override val millijouleScale: Long = 1_000_000_000_000_000
            override val symbol: String = "TJ"
        }
        public val Petajoule: EnergyUnit = object : EnergyUnit {
            override val millijouleScale: Long = 1_000_000_000_000_000_000
            override val symbol: String = "PJ"
        }
        public val entries: List<EnergyUnit> = listOf(Millijoule, Joule, Kilojoule, Megajoule, Gigajoule, Tetrajoule, Petajoule)
    }

    /**
     * A non-standard representation of energy commonly used to measure electrical energy.
     */
    public object Electricity {
        public val MilliwattHour: EnergyUnit = object : EnergyUnit {
            override val millijouleScale: Long = 3_600
            override val symbol: String = "mWh"
        }
        public val WattHour: EnergyUnit = object : EnergyUnit {
            override val millijouleScale: Long = 3_600_000
            override val symbol: String = "Wh"
        }
        public val KilowattHour: EnergyUnit = object : EnergyUnit {
            override val millijouleScale: Long = 3_600_000_000
            override val symbol: String = "kWh"
        }
        public val MegawattHour: EnergyUnit = object : EnergyUnit {
            override val millijouleScale: Long = 3_600_000_000_000
            override val symbol: String = "MWh"
        }
        public val GigawattHour: EnergyUnit = object : EnergyUnit {
            override val millijouleScale: Long = 3_600_000_000_000_000
            override val symbol: String = "GWh"
        }
        public val TerawattHour: EnergyUnit = object : EnergyUnit {
            override val millijouleScale: Long = 3_600_000_000_000_000_000
            override val symbol: String = "TWh"
        }
        public val entries: List<EnergyUnit> = listOf(MilliwattHour, WattHour, KilowattHour, MegawattHour, GigawattHour, TerawattHour)
    }
}
