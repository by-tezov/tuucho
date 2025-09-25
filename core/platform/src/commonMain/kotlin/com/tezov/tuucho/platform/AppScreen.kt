package com.tezov.tuucho.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.tezov.tuucho.core.presentation.ui.renderer.rememberTuuchoEngine
import com.tezov.tuucho.platform.di.StartKoinModules
import org.koin.dsl.ModuleDeclaration

@Composable
fun AppScreen(
    applicationModuleDeclaration: ModuleDeclaration
) = StartKoinModules(applicationModuleDeclaration) {
    val tuuchoEngine = rememberTuuchoEngine()
    LaunchedEffect(Unit) {
        tuuchoEngine.load(url = "config")
        tuuchoEngine.start(url = "page-home")
    }
    tuuchoEngine.display()
}
