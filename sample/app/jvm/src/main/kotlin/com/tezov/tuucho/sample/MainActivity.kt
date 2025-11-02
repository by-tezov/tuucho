package com.tezov.tuucho.sample

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.tezov.tuucho.sample.di.ApplicationModule
import com.tezov.tuucho.shared.sample.AppScreen

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Tuucho Desktop") {
        AppScreen(listOf(ApplicationModule.invoke()))
    }
}

