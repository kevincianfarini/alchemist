package io.github.kevincianfarini.alchemist

import kotlinx.cinterop.*
import platform.posix.sprintf

@OptIn(ExperimentalForeignApi::class)
internal actual fun Double.toDecimalString(decimals: Int): String = memScoped {
    val buff = allocArray<ByteVar>(Double.SIZE_BYTES * 2)
    sprintf(buff, "%.${decimals}f", this@toDecimalString)
    buff.toKString()
}