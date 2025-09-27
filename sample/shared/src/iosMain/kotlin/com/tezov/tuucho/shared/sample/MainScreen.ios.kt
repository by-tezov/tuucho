package com.tezov.tuucho.shared.sample

import androidx.compose.ui.window.ComposeUIViewController

fun uiView() = ComposeUIViewController {
    AppScreen(applicationModuleDeclaration = {})
}