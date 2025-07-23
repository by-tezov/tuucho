package com.tezov.tuucho.kmm

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tezov.tuucho.core.domain.usecase.ComponentRenderUseCase
import com.tezov.tuucho.core.domain.usecase.RefreshCacheMaterialUseCase
import com.tezov.tuucho.core.ui.renderer._system.ComposableScreenProtocol
import com.tezov.tuucho.kmm.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

actual fun MainScreen.getScreen(): ComposableScreenProtocol = object : ComposableScreenProtocol() {

    @Composable
    override fun show(scope: Any?) {
        /* TODO: that part will move on application launch, it has nothing to do here,
            but for now, it will be here
        */
        var ready by remember { mutableStateOf(false) }
        val refreshCacheMaterial: RefreshCacheMaterialUseCase = koinInject()
        LaunchedEffect(Unit) {
            refreshCacheMaterial.invoke("config")
            ready = true
        }
        /* end hack */

        if (!ready) {
            Text("Not ready...")
        } else {
            AppTheme { StartEngineScreen() }
        }

    }

    @Composable
    fun StartEngineScreen(
        viewModel: MainViewModel = koinViewModel()
    ) {
        var screen by remember { mutableStateOf<ComposableScreenProtocol?>(null) }
        val renderer = koinInject<ComponentRenderUseCase>()

        LaunchedEffect(Unit) { viewModel.init() }

        LaunchedEffect(viewModel.url.value) {
            screen = renderer.invoke(viewModel.url.value) as? ComposableScreenProtocol
        }
        screen?.show(null)
    }
}
