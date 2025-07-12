package io.github.kevincianfarini.alchemist.unit

import kotlin.text.Typography.nbsp

/**
 * A unit of volume precise to the centimeter³.
 */
public interface VolumeUnit {

    /**
     * The symbol of this unit.
     */
    public val symbol: String

    /**
     * The amount of centimeter³ in this unit. Implementations of [TemperatureUnit] should be perfectly divisible by a
     * quantity of centimeter³.
     */
    public val cubicCentimetersScale: Long

    /**
     * A non-standard representation of volume commonly used as part of the metric system.
     */
    public object Metric {
        public val Milliliter: VolumeUnit = object : VolumeUnit {
            override val symbol: String = "${nbsp}mL"
            override val cubicCentimetersScale: Long = 1
        }
        public val Liter: VolumeUnit = object : VolumeUnit {
            override val symbol: String = "${nbsp}L"
            override val cubicCentimetersScale: Long = 1_000
        }
        public val Kiloliter: VolumeUnit = object : VolumeUnit {
            override val symbol: String = "${nbsp}kL"
            override val cubicCentimetersScale: Long = 1_000_000
        }
        public val Megaliter: VolumeUnit = object : VolumeUnit {
            override val symbol: String = "${nbsp}ML"
            override val cubicCentimetersScale: Long = 1_000_000_000
        }
        public val Gigaliter: VolumeUnit = object : VolumeUnit {
            override val symbol: String = "${nbsp}GL"
            override val cubicCentimetersScale: Long = 1_000_000_000_000
        }
        public val Teraliter: VolumeUnit = object : VolumeUnit {
            override val symbol: String = "${nbsp}TL"
            override val cubicCentimetersScale: Long = 1_000_000_000_000_000
        }
        public val Petaliter: VolumeUnit = object : VolumeUnit {
            override val symbol: String = "${nbsp}PL"
            override val cubicCentimetersScale: Long = 1_000_000_000_000_000_000
        }
        public val entries: List<VolumeUnit> = listOf(Milliliter, Liter, Kiloliter, Megaliter, Gigaliter, Teraliter, Petaliter)
    }
}
