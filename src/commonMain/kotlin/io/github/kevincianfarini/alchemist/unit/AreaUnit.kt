package io.github.kevincianfarini.alchemist.unit


/**
 * A unit of area precise to the millimeter².
 */
public interface AreaUnit {

    /**
     * The amount of millimeters² in this unit. Implementations of [AreaUnit] should be perfectly divisible by a
     * quantity of millimeters².
     */
    public val millimetersSquaredScale: Long

    /**
     * The symbol of this unit.
     */
    public val symbol: String

    /**
     * A non-standard representation of area commonly used as part of the metric system.
     */
    public object Metric {
        public val Decimilliare: AreaUnit = object : AreaUnit {
            override val symbol: String = "dma"
            override val millimetersSquaredScale: Long = 100
        }
        public val Centiare: AreaUnit = object : AreaUnit {
            override val symbol: String = "ca"
            override val millimetersSquaredScale: Long = 1_000_000
        }
        public val Deciare: AreaUnit = object : AreaUnit {
            override val symbol: String = "da"
            override val millimetersSquaredScale: Long = 10_000_000
        }
        public val Are: AreaUnit = object : AreaUnit {
            override val symbol: String = "a"
            override val millimetersSquaredScale: Long = 100_000_000
        }
        public val Decare: AreaUnit = object : AreaUnit {
            override val symbol: String = "daa"
            override val millimetersSquaredScale: Long = 1_000_000_000
        }
        public val Hectare: AreaUnit = object : AreaUnit {
            override val symbol: String = "ha"
            override val millimetersSquaredScale: Long = 10_000_000_000
        }
        public val entries: List<AreaUnit> = listOf(Decimilliare, Centiare, Deciare, Are, Decare, Hectare)
    }
}
