package com.tezov.tuucho.sample.shared

import androidx.compose.ui.window.ComposeUIViewController

fun uiView(
    koinExtension: (KoinApplication.() -> Unit)? = null,
) = ComposeUIViewController {
    AppScreen(
        applicationModules = emptyList(),
        koinExtension = koinExtension
    )
}