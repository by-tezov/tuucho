package com.tezov.tuucho.shared.sample

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.tezov.tuucho.core.barrel.di.StartKoinModules
import com.tezov.tuucho.core.presentation.ui.renderer.TuuchoEngine.Companion.rememberTuuchoEngine
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
        tuuchoEngine.start(url = "lobby/page-login")
    }
    tuuchoEngine.display()
}
