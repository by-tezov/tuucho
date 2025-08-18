package com.tezov.tuucho.kmm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.tezov.tuucho.core.data.database.dao.JsonObjectQueries
import com.tezov.tuucho.core.data.database.dao.VersioningQueries
import com.tezov.tuucho.core.presentation.ui.renderer.rememberTuuchoEngine
import com.tezov.tuucho.kmm.di.StartKoinModules
import org.koin.dsl.ModuleDeclaration
import org.koin.mp.KoinPlatform.getKoin

@Composable
fun AppScreen(
    moduleDeclaration: ModuleDeclaration
) = StartKoinModules(moduleDeclaration) {
    val tuuchoEngine = rememberTuuchoEngine()
    LaunchedEffect(Unit) {
        //TODO remove when loading, migration upgrade/auto purge is done
        getKoin().get<JsonObjectQueries>().deleteAll()
        getKoin().get<VersioningQueries>().deleteAll()
        //***************************************************************
        tuuchoEngine.init(
            configUrl = "config",
            initialUrl = "page-home"
        )
    }
    tuuchoEngine.display()
}
