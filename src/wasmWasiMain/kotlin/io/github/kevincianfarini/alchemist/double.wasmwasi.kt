@file:Suppress(
    "LocalVariableName",
    "JoinDeclarationAndAssignment",
    "CanBeVal",
    "FunctionName",
    "LiftReturnOrAssignment"
)

package io.github.kevincianfarini.alchemist

import kotlin.math.pow

internal actual fun Double.toDecimalString(decimals: Int): String {
    // Copy-pasted impl from the stdlib, which has an open issue: (KT-60964).
    val pow = 10.0.pow(decimals)
    val round = rint(this * pow)
    return (round / pow).toString()
}

private val TWO52 = doubleArrayOf(
    4.50359962737049600000e+15, /* 0x43300000, 0x00000000 */
    -4.50359962737049600000e+15, /* 0xC3300000, 0x00000000 */
)

private fun rint(_x: Double): Double {
    var x: Double = _x
    var i0: Int
    var j0: Int
    var sx: Int
    var i: UInt
    var i1: UInt
    var w: Double
    var t: Double

    i0 = __HI(x)
    sx = (i0 shr 31) and 1
    i1 = __LOu(x)
    j0 = ((i0 shr 20) and 0x7ff) - 0x3ff
    if (j0 < 20) {
        if (j0 < 0) {
            if (((i0 and 0x7fffffff) or i1.toInt()) == 0) return x
            i1 = i1 or (i0 and 0x0fffff).toUInt()
            i0 = i0 and 0xfffe0000.toInt()
            i0 = i0 or (((i1 or i1.negate()) shr 12) and 0x80000.toUInt()).toInt()
            x = doubleSetWord(d = x, hi = i0)
            w = TWO52[sx] + x
            t = w - TWO52[sx]
            i0 = __HI(t)
            t = doubleSetWord(d = t, hi = (i0 and 0x7fffffff) or (sx shl 31))
            return t
        } else {
            i = ((0x000fffff) shr j0).toUInt()
            if (((i0 and i.toInt()) or i1.toInt()) == 0) return x /* x is integral */
            i = i shr 1
            if (((i0 and i.toInt()) or i1.toInt()) != 0) {
                if (j0 == 19) i1 = 0x40000000.toUInt(); else
                    i0 = (i0 and i.inv().toInt()) or ((0x20000) shr j0)
            }
        }
    } else if (j0 > 51) {
        if (j0 == 0x400) return x + x    /* inf or NaN */
        else return x        /* x is integral */
    } else {
        i = ((0xffffffff.toUInt())) shr (j0 - 20)
        if ((i1 and i) == 0U) return x    /* x is integral */
        i = i shr 1
        if ((i1 and i) != 0U) i1 = (i1 and (i.inv())) or ((0x40000000) shr (j0 - 20)).toUInt()
    }
    x = doubleSetWord(x, hi = i0, lo = i1.toInt())
    w = TWO52[sx] + x
    return w - TWO52[sx]
}

private fun __HI(x: Double): Int = (x.toRawBits() ushr 32).toInt()

//#define __LO(x) *(int*)&x
private fun __LO(x: Double): Int = (x.toRawBits() and 0xFFFFFFFF).toInt()
private fun __LOu(x: Double): UInt = (x.toRawBits() and 0xFFFFFFFF).toUInt()

private fun doubleSetWord(d: Double = 0.0, hi: Int = __HI(d), lo: Int = __LO(d)): Double =
    Double.fromBits((hi.toLong() shl 32) or (lo.toLong() and 0xFFFFFFFF))

private fun UInt.negate(): UInt = inv() + 1U
