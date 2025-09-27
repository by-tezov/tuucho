package com.tezov.tuucho.shared.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.tezov.tuucho.core.presentation.ui.renderer.rememberTuuchoEngine
import com.tezov.tuucho.barrel.di.StartKoinModules
import com.tezov.tuucho.shared.sample.di.SystemSharedModules
import org.koin.dsl.ModuleDeclaration

@Composable
fun AppScreen(
    applicationModuleDeclaration: ModuleDeclaration,
) = StartKoinModules(
    SystemSharedModules.invoke() + applicationModuleDeclaration
) {
    val tuuchoEngine = rememberTuuchoEngine()
    LaunchedEffect(Unit) {
        tuuchoEngine.load(url = "config")
        tuuchoEngine.start(url = "page-home")
    }
    tuuchoEngine.display()
}
