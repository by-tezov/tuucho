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
import com.tezov.tuucho.core.domain.usecase.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.usecase.RenderComponentUseCase
import com.tezov.tuucho.core.ui.uiComponentFactory._system.ViewProtocol
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun mainScreen() {

    /* TODO: that part will move on application launch, it has nothing to do here,
        but for now, it will be here
    */
    var ready by remember { mutableStateOf(false) }
    val refreshMaterialCache = remember { getKoin().get<RefreshMaterialCacheUseCase>() }

    LaunchedEffect(Unit) {
        //TODO remove when loading, migration upgrade/auto purge is done
        getKoin().get<JsonObjectQueries>().deleteAll()
        getKoin().get<VersioningQueries>().deleteAll()
        refreshMaterialCache.invoke("config")
        ready = true
        //***********************************************
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
    var screen by remember { mutableStateOf<ViewProtocol?>(null) }
    val renderer = remember { getKoin().get<RenderComponentUseCase>() }

    LaunchedEffect(Unit) { viewModel.init() }
    LaunchedEffect(viewModel.url.value) {
        screen = renderer.invoke(viewModel.url.value) as? ViewProtocol
    }
    screen?.display(null)
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onDispose()
        }
    }
}