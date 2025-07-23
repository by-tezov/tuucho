package com.tezov.tuucho.core.ui._system

import androidx.compose.ui.graphics.Color
import kotlin.error

fun String.toColor() = toColorOrNull() ?: error("Unsupported color format")

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