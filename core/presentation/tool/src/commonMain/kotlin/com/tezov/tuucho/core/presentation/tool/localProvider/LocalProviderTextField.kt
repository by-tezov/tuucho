package com.tezov.tuucho.core.presentation.tool.localProvider

import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.TextToolbar
import androidx.compose.ui.platform.TextToolbarStatus

object LocalProviderTextField {

    fun textSelectionColors(handleColor: Color, backgroundColor: Color) =
        LocalTextSelectionColors provides TextSelectionColors(
            handleColor = handleColor,
            backgroundColor = backgroundColor
        )

    val textSelectionColorsNoColor
        get() = textSelectionColors(
            handleColor = Color.Transparent,
            backgroundColor = Color.Transparent
        )

    val textSelectionToolbarDisabled
        get() = LocalTextToolbar provides object : TextToolbar {
            override val status: TextToolbarStatus = TextToolbarStatus.Hidden

            override fun hide() {}

            override fun showMenu(
                rect: Rect,
                onCopyRequested: (() -> Unit)?,
                onPasteRequested: (() -> Unit)?,
                onCutRequested: (() -> Unit)?,
                onSelectAllRequested: (() -> Unit)?,
            ) {
            }
        }

    val keyboardDisabled get() = LocalTextInputService provides null

}