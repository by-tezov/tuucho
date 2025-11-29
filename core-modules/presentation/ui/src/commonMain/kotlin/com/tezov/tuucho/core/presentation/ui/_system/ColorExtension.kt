@file:Suppress("ktlint:standard:package-name")

package com.tezov.tuucho.core.presentation.ui._system

import androidx.compose.ui.graphics.Color

fun String.toColor() = toColorOrNull() ?: throw _root_ide_package_.com.tezov.tuucho.core.presentation.ui.exception.UiException.Default(
    "Unsupported color format"
)

fun String.toColorOrNull(): Color? {
    if (!startsWith("#")) return null
    val hex = removePrefix("#")
    val baseColor = hex.toLongOrNull(16)?.toInt() ?: return null
    return when (hex.length) {
        6 -> baseColor or 0xFF000000.toInt()
        8 -> baseColor
        else -> return null
    }.let(::Color)
}
