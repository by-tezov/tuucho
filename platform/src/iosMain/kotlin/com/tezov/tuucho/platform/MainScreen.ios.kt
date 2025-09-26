package com.tezov.tuucho.platform

import androidx.compose.ui.window.ComposeUIViewController
import org.koin.dsl.ModuleDeclaration

fun uiView(
    applicationModuleDeclaration: ModuleDeclaration
) = ComposeUIViewController {
    AppScreen(applicationModuleDeclaration)
}