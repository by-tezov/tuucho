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
import com.tezov.tuucho.core.domain.business.usecase.GetLastViewUseCase
import com.tezov.tuucho.core.domain.business.usecase.RefreshMaterialCacheUseCase
import com.tezov.tuucho.core.domain.business.usecase._system.UseCaseExecutor
import com.tezov.tuucho.core.presentation.ui.viewFactory._system.ViewProtocol
import com.tezov.tuucho.kmm.di.StartKoinModules
import org.koin.compose.koinInject
import org.koin.dsl.ModuleDeclaration
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun AppScreen(
    moduleDeclaration: ModuleDeclaration,
) = StartKoinModules(moduleDeclaration) {
    /* TODO: that part will move on application launch, it has nothing to do here,
        but for now, it will be here
    */
    var ready by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        //TODO remove when loading, migration upgrade/auto purge is done
        getKoin().get<JsonObjectQueries>().deleteAll()
        getKoin().get<VersioningQueries>().deleteAll()
        val useCaseExecutor = getKoin().get<UseCaseExecutor>()
        useCaseExecutor.invokeSuspend(
            useCase = getKoin().get<RefreshMaterialCacheUseCase>(),
            input = RefreshMaterialCacheUseCase.Input(
                url = "config"
            )
        )
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
    viewModel: AppScreenViewModel = koinInject(),
) {
    var view by remember { mutableStateOf<ViewProtocol?>(null) }
    LaunchedEffect(Unit) {
        viewModel.init()
    }
    LaunchedEffect(viewModel.url) {
        if (viewModel.url.isNotBlank()) {
            val useCaseExecutor = getKoin().get<UseCaseExecutor>()
            view = useCaseExecutor.invokeSuspend(
                useCase = getKoin().get<GetLastViewUseCase>(),
                input = Unit,
            ).view as? ViewProtocol
        }
    }
    view?.display()
    DisposableEffect(Unit) {
        onDispose {
            viewModel.onDispose()
        }
    }
}