package com.tezov.tuucho.core.presentation.tool.type.primaire

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.size(
    size: DpSize
): Modifier = if (size.width != 0.dp && size.height != 0.dp) {
    width(size.width).height(size.height)
} else if (size.width != 0.dp) {
    width(size.width).aspectRatio(1.0f)
} else {
    height(size.height).aspectRatio(1.0f)
}

inline val Int.dpSize: DpSize get() = DpSize(this)
inline val Double.dpSize: DpSize get() = DpSize(this)

class DpSize(
    var width: Dp = 0.dp,
    var height: Dp = 0.dp,
    var padding: PaddingValues? = null,
) {
    constructor(size: Dp) : this(size, size)
    constructor(size: Int) : this(size.dp, size.dp)
    constructor(size: Double) : this(size.dp, size.dp)

    fun swap() {
        height = width.also { width = height }
    }

    val ratio
        get() = if (width.value != 0f) {
            height.value / width.value
        } else {
            Float.NaN
        }

    val isSquare get() = width == height

    val square get() = if (isSquare) width else null

    val squareMax get() = if (width < height) height else width

    val squareMin get() = if (width < height) width else height

    val radius get() = square?.let { it / 2 }

    val radiusMax get() = squareMax / 2

    val radiusMin get() = squareMin / 2

    fun scaleTo(
        s: Scale
    ) {
        width *= s.w
        height *= s.h
    }

    fun scaleFrom(
        s: Scale
    ) {
        width /= s.w
        height /= s.h
    }
}
