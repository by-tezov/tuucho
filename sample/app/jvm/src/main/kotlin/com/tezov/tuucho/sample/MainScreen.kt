package com.tezov.tuucho.sample

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.tezov.tuucho.core.domain.business._system.koin.TuuchoKoinComponent
import com.tezov.tuucho.sample.shared.AppScreen
import com.tezov.tuucho.sample.shared.middleware.navigateFinish.NavigationFinishPublisher

fun main() = application {
    val koinComponent = object : TuuchoKoinComponent {}
    Window(
        onCloseRequest = { exit(koinComponent) },
        title = "Tuucho Sample Application",
        state = WindowState(width = 512.dp, height = 912.dp)
    ) {
        AppScreen(
            applicationModules = emptyList(),
            koinExtension = {
                koin.get<NavigationFinishPublisher>().onFinish {
                    exit(koinComponent)
                }
            }
        )
    }
}

private fun ApplicationScope.exit(
    koinComponent: TuuchoKoinComponent
) {
    koinComponent.getKoin().close()
    exitApplication()
}

