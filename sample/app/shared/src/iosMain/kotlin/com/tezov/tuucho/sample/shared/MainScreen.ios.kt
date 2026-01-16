package com.tezov.tuucho.sample.shared

import androidx.compose.ui.window.ComposeUIViewController
import com.tezov.tuucho.sample.shared.middleware.navigateFinish.NavigationFinishPublisher
import org.koin.core.KoinApplication

fun uiView(
    koinExtension: (KoinApplication.() -> Unit)? = null,
) = ComposeUIViewController {
    AppScreen(
        applicationModules = emptyList(),
        koinExtension = koinExtension
    )
}

fun KoinApplication.getNavigationFinishPublisher(): NavigationFinishPublisher = koin.get()