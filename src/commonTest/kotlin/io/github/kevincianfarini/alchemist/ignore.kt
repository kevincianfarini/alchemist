@file:OptIn(ExperimentalMultiplatform::class)

package io.github.kevincianfarini.alchemist

/**
 * Ignores a test for the Js target.
 */
@OptionalExpectation
expect annotation class JsIgnore()

/**
 * Ignores a test for the WasmJs target.
 */
@OptionalExpectation
expect annotation class WasmJsIgnore()

/**
 * Ignores a test for the WasmWasi target.
 */
@OptionalExpectation
expect annotation class WasmWasiIgnore()