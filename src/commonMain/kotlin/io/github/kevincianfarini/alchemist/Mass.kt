package io.github.kevincianfarini.alchemist

import kotlin.jvm.JvmInline
import kotlin.text.Typography.nbsp

@JvmInline
public value class Mass internal constructor(private val rawMicrograms: SaturatingLong) : Comparable<Mass> {

    // region SI Arithmetic

    /**
     * Returns the [Force] required to apply to this mass to achieve the specified [acceleration].
     */
    public operator fun times(acceleration: Acceleration): Force = TODO()

    // endregion

    // region Scalar Arithmetic

    /**
     * Returns a mass whose value is this mass value divided by the specified [scale].
     */
    public operator fun div(scale: Int): Mass = div(scale.toLong())

    /**
     * Returns a mass whose value is this mass value divided by the specified [scale].
     */
    public operator fun div(scale: Long): Mass = Mass(rawMicrograms / scale)

    /**
     * Returns the number that is the ratio of this and the [other] mass value.
     */
    public operator fun div(other: Mass): Double = rawMicrograms.toDouble() / other.rawMicrograms.toDouble()

    /**
     * Returns a mass whose value is the difference between this and the [other] mass value.
     */
    public operator fun minus(other: Mass): Mass = Mass(rawMicrograms - other.rawMicrograms)

    /**
     * Returns a mass whose value is the sum between this and the [other] mass value.
     */
    public operator fun plus(other: Mass): Mass = Mass(rawMicrograms + other.rawMicrograms)

    /**
     * Returns a mass whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Int): Mass = times(scale.toLong())

    /**
     * Returns a mass whose value is multiplied by the specified [scale].
     */
    public operator fun times(scale: Long): Mass = Mass(rawMicrograms * scale)

    // endregion

    // region Mass to Scalar Conversions

    public fun <T> toInternationalComponents(
        action: (
            teragrams: Long,
            gigagrams: Long,
            megagrams: Long,
            kilograms: Long,
            grams: Long,
            milligrams: Long,
            micrograms: Long,
        ) -> T
    ): T {
        val tera = rawMicrograms / MassUnit.International.Teragram.microgramScale
        val teraRemainder = rawMicrograms % MassUnit.International.Teragram.microgramScale
        val giga = teraRemainder / MassUnit.International.Gigagram.microgramScale
        val gigaRemainder = teraRemainder % MassUnit.International.Gigagram.microgramScale
        val mega = gigaRemainder / MassUnit.International.Megagram.microgramScale
        val megaRemainder = gigaRemainder % MassUnit.International.Megagram.microgramScale
        val kilo = megaRemainder / MassUnit.International.Kilogram.microgramScale
        val kiloRemainder = megaRemainder % MassUnit.International.Kilogram.microgramScale
        val grams = kiloRemainder / MassUnit.International.Gram.microgramScale
        val gramRemainder = kiloRemainder % MassUnit.International.Gram.microgramScale
        val milli = gramRemainder / MassUnit.International.Milligram.microgramScale
        val micro = gramRemainder % MassUnit.International.Megagram.microgramScale
        return action(
            tera.rawValue,
            giga.rawValue,
            mega.rawValue,
            kilo.rawValue,
            grams.rawValue,
            milli.rawValue,
            micro.rawValue
        )
    }

    public fun toDouble(unit: MassUnit): Double {
        return rawMicrograms.toDouble() / unit.microgramScale.toDouble()
    }

    public fun toString(unit: MassUnit, decimals: Int = 0): String = when (isInfinite()) {
        true -> rawMicrograms.toString()
        false -> buildString {
            append(toDouble(unit).toDecimalString(decimals))
            append(nbsp)
            append(unit.symbol)
        }
    }

    override fun toString(): String {
        val largestUnit = MassUnit.International.entries.asReversed().firstOrNull { unit ->
            rawMicrograms.absoluteValue / unit.microgramScale > 0
        }
        return toString(largestUnit ?: MassUnit.International.Microgram, decimals = 2)
    }

    // endregion

    // region Comparisons

    public fun isInfinite(): Boolean = rawMicrograms.isInfinite()

    public fun isFinite(): Boolean = rawMicrograms.isFinite()

    override fun compareTo(other: Mass): Int = rawMicrograms.compareTo(other.rawMicrograms)

    // endregion

    public companion object {
        public val POSITIVE_INFINITY: Mass = Mass(SaturatingLong.POSITIVE_INFINITY)
        public val NEGATIVE_INFINITY: Mass = Mass(SaturatingLong.NEGATIVE_INFINITY)
    }
}

// region Scalar to Mass Conversions

public fun Int.toMass(unit: MassUnit): Mass {
    return toLong().toMass(unit)
}

public fun Long.toMass(unit: MassUnit): Mass {
    return Mass(saturated * unit.microgramScale)
}

public inline val Int.micrograms: Mass get() = toMass(MassUnit.International.Microgram)
public inline val Long.micrograms: Mass get() = toMass(MassUnit.International.Microgram)

public inline val Int.milligrams: Mass get() = toMass(MassUnit.International.Milligram)
public inline val Long.milligrams: Mass get() = toMass(MassUnit.International.Milligram)

public inline val Int.grams: Mass get() = toMass(MassUnit.International.Gram)
public inline val Long.grams: Mass get() = toMass(MassUnit.International.Gram)

public inline val Int.kilograms: Mass get() = toMass(MassUnit.International.Kilogram)
public inline val Long.kilograms: Mass get() = toMass(MassUnit.International.Kilogram)

public inline val Int.megagrams: Mass get() = toMass(MassUnit.International.Megagram)
public inline val Long.megagrams: Mass get() = toMass(MassUnit.International.Megagram)

public inline val Int.metricTonnes: Mass get() = toMass(MassUnit.International.Megagram)
public inline val Long.metricTonnes: Mass get() = toMass(MassUnit.International.Megagram)

public inline val Int.gigagrams: Mass get() = toMass(MassUnit.International.Gigagram)
public inline val Long.gigagrams: Mass get() = toMass(MassUnit.International.Gigagram)

public inline val Int.teragrams: Mass get() = toMass(MassUnit.International.Teragram)
public inline val Long.teragrams: Mass get() = toMass(MassUnit.International.Teragram)

// endregion

public interface MassUnit {

    public val microgramScale: Long

    public val symbol: String

    public enum class International(override val microgramScale: Long, override val symbol: String): MassUnit {
        Microgram(1, "μg"),
        Milligram(1_000, "mg"),
        Gram(1_000_000, "g"),
        Kilogram(1_000_000_000, "kg"),
        Megagram(1_000_000_000_000, "Mg"),
        Gigagram(1_000_000_000_000_000, "Gg"),
        Teragram(1_000_000_000_000_000_000, "Tg"),
    }
}
