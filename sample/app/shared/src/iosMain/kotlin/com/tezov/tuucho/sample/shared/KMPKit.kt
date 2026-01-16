package com.tezov.tuucho.sample.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.tezov.tuucho.core.barrel.di.KoinIos
import org.koin.core.KoinApplication

fun uiView(
    koinExtension: (KoinApplication.() -> Unit)? = null,
) = ComposeUIViewController {
    AppScreen(
        applicationModules = emptyList(),
        koinExtension = koinExtension
    )
}

val KoinApplication.tuuchoKoinIos get(): KoinIos = koin.get<KoinIos>()

