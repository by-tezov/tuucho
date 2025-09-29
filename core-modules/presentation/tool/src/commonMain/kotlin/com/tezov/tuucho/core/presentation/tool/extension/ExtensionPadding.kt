package com.tezov.tuucho.core.presentation.tool.extension

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

object ExtensionPadding {

    inline val PaddingValues.start @Composable get() = this.start(LocalLayoutDirection.current)
    fun PaddingValues.start(layoutDirection: LayoutDirection) =
        calculateStartPadding(layoutDirection)

    inline val PaddingValues.end @Composable get() = this.end(LocalLayoutDirection.current)
    fun PaddingValues.end(layoutDirection: LayoutDirection) = calculateStartPadding(layoutDirection)

    inline val PaddingValues.top @Composable get() = this.top(LocalLayoutDirection.current)
    fun PaddingValues.top(layoutDirection: LayoutDirection) = calculateStartPadding(layoutDirection)

    inline val PaddingValues.bottom @Composable get() = this.bottom(LocalLayoutDirection.current)
    fun PaddingValues.bottom(layoutDirection: LayoutDirection) =
        calculateStartPadding(layoutDirection)

}