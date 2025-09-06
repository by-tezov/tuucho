package com.tezov.tuucho.kmm

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.tezov.tuucho.core.presentation.ui.renderer.rememberTuuchoEngine
import com.tezov.tuucho.kmm.di.StartKoinModules
import org.koin.dsl.ModuleDeclaration

@Composable
fun AppScreen(
    moduleDeclaration: ModuleDeclaration
) = StartKoinModules(moduleDeclaration) {
    val tuuchoEngine = rememberTuuchoEngine()
    LaunchedEffect(Unit) {
        tuuchoEngine.init(
            configUrl = "config",
            initialUrl = "page-home"
        )
    }
    tuuchoEngine.display()
}
