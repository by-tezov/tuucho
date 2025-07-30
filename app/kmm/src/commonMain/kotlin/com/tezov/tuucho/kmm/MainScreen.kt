package com.tezov.tuucho.kmm

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tezov.tuucho.core.data.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.database.dao.VersioningQueries
import com.tezov.tuucho.core.domain.usecase.ComponentRenderUseCase
import com.tezov.tuucho.core.domain.usecase.RefreshCacheMaterialUseCase
import com.tezov.tuucho.core.ui.composable._system.ComposableScreenProtocol
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun mainScreen() {

    /* TODO: that part will move on application launch, it has nothing to do here,
        but for now, it will be here
    */
    var ready by remember { mutableStateOf(false) }
    val refreshCacheMaterial = remember { getKoin().get<RefreshCacheMaterialUseCase>() }

    LaunchedEffect(Unit) {
        //TODO remove when loading, migration upgrade/auto purge is done
        getKoin().get<JsonObjectQueries>().clearAll()
        getKoin().get<VersioningQueries>().clearAll()
        //***********************************************

        refreshCacheMaterial.invoke("config")
        ready = true
    }
    /* end hack */

    if (!ready) {
        Text("Not ready...")
    } else {
        StartEngineScreen()
    }
}

@Composable
private fun StartEngineScreen(
    viewModel:MainViewModel = remember { getKoin().get<MainViewModel>() }
) {
    var screen by remember { mutableStateOf<ComposableScreenProtocol?>(null) }
    val renderer = remember { getKoin().get<ComponentRenderUseCase>() }

    LaunchedEffect(Unit) { viewModel.init() }
    LaunchedEffect(viewModel.url.value) {
        screen = renderer.invoke(viewModel.url.value) as? ComposableScreenProtocol
    }

    screen?.show(null)

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onCleared()
        }
    }
}