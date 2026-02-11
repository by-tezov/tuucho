package com.tezov.tuucho.sample.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.tezov.tuucho.core.barrel.di.KoinIos
import com.tezov.tuucho.core.domain.business._system.koin.KoinIsolatedContext
import org.koin.core.Koin
import org.koin.core.KoinApplication

fun uiView(
    koinExtension: (KoinApplication.() -> Unit)? = null,
) = ComposeUIViewController {
    AppScreen(
        applicationModules = emptyList(),
        koinExtension = koinExtension
    )
}

val koinIsolatedContext: KoinIsolatedContext get() = KoinIsolatedContext

val Koin.iOS get(): KoinIos = get<KoinIos>()
