package com.tezov.tuucho.platform

import androidx.compose.ui.window.ComposeUIViewController
import com.tezov.tuucho.data.platform.AppScreen

fun uiView() = ComposeUIViewController {
    AppScreen({})
}