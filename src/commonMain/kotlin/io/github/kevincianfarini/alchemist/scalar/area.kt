package io.github.kevincianfarini.alchemist.scalar

import io.github.kevincianfarini.alchemist.internal.SaturatingLong
import io.github.kevincianfarini.alchemist.internal.saturated
import io.github.kevincianfarini.alchemist.type.Area
import io.github.kevincianfarini.alchemist.unit.AreaUnit
import kotlin.math.roundToLong

/**
 * Returns an [Area] equal to [Int] number of decimilliares.
 */
public inline val Int.decimilliares: Area get() = toArea(AreaUnit.Metric.Decimilliare)

/**
 * Returns an [Area] equal to [Long] number of decimilliares.
 */
public inline val Long.decimilliares: Area get() = toArea(AreaUnit.Metric.Decimilliare)

/**
 * Returns an [Area] equal to [Double] number of decimilliares. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.decimilliares: Area get() = toArea(AreaUnit.Metric.Decimilliare)

/**
 * Returns an [Area] equal to [Int] number of centiares.
 */
public inline val Int.centiares: Area get() = toArea(AreaUnit.Metric.Centiare)

/**
 * Returns an [Area] equal to [Long] number of centiares.
 */
public inline val Long.centiares: Area get() = toArea(AreaUnit.Metric.Centiare)

/**
 * Returns an [Area] equal to [Double] number of centiares. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.centiares: Area get() = toArea(AreaUnit.Metric.Centiare)

/**
 * Returns an [Area] equal to [Int] number of deciares.
 */
public inline val Int.deciares: Area get() = toArea(AreaUnit.Metric.Deciare)

/**
 * Returns an [Area] equal to [Long] number of deciares.
 */
public inline val Long.deciares: Area get() = toArea(AreaUnit.Metric.Deciare)

/**
 * Returns an [Area] equal to [Double] number of deciares. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.deciares: Area get() = toArea(AreaUnit.Metric.Deciare)

/**
 * Returns an [Area] equal to [Int] number of ares.
 */
public inline val Int.ares: Area get() = toArea(AreaUnit.Metric.Are)

/**
 * Returns an [Area] equal to [Long] number of ares.
 */
public inline val Long.ares: Area get() = toArea(AreaUnit.Metric.Are)

/**
 * Returns an [Area] equal to [Double] number of ares. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.ares: Area get() = toArea(AreaUnit.Metric.Are)

/**
 * Returns an [Area] equal to [Int] number of decares.
 */
public inline val Int.decares: Area get() = toArea(AreaUnit.Metric.Decare)

/**
 * Returns an [Area] equal to [Long] number of decares.
 */
public inline val Long.decares: Area get() = toArea(AreaUnit.Metric.Decare)

/**
 * Returns an [Area] equal to [Double] number of decares. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.decares: Area get() = toArea(AreaUnit.Metric.Decare)

/**
 * Returns an [Area] equal to [Int] number of hectares.
 */
public inline val Int.hectares: Area get() = toArea(AreaUnit.Metric.Hectare)

/**
 * Returns an [Area] equal to [Long] number of hectares.
 */
public inline val Long.hectares: Area get() = toArea(AreaUnit.Metric.Hectare)

/**
 * Returns an [Area] equal to [Double] number of hectares. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public inline val Double.hectares: Area get() = toArea(AreaUnit.Metric.Hectare)

/**
 * Returns an [Area] equal to [Int] number of the specified [unit].
 */
public fun Int.toArea(unit: AreaUnit): Area = toLong().toArea(unit)

/**
 * Returns an [Area] equal to [Long] number of the specified [unit].
 */
public fun Long.toArea(unit: AreaUnit): Area = Area(saturated * unit.millimetersSquaredScale)

/**
 * Returns an [Area] equal to [Double] number of the specified [unit]. Depending on its magnitude, some precision may be
 * lost.
 *
 * @throws IllegalArgumentException is this [Double] is [Double.NaN].
 */
public fun Double.toArea(unit: AreaUnit): Area {
    val valueInMillimeters2 = this * unit.millimetersSquaredScale
    require(!valueInMillimeters2.isNaN()) { "Area value cannot be NaN." }
    return Area(valueInMillimeters2.roundToLong().saturated)
}

internal inline val Long.mm2: Area get() = Area(saturated)
internal inline val Int.mm2: Area get() = Area(toLong().saturated)
internal inline val SaturatingLong.mm2: Area get() = Area(this)
