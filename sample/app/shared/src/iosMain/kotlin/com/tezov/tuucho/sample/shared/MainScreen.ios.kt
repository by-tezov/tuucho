package com.tezov.tuucho.sample.shared

import androidx.compose.ui.window.ComposeUIViewController

fun uiView() = ComposeUIViewController {
    AppScreen(applicationModules = emptyList())
}
