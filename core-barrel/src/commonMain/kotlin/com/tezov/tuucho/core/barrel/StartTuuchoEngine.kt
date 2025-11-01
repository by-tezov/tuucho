package com.tezov.tuucho.core.barrel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.tezov.tuucho.core.barrel.di.SystemCoreModules
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.presentation.ui.renderer.TuuchoEngineProtocol

@Composable
fun StartTuuchoEngine(
    applicationModules: List<ModuleProtocol>,
    onStartUrl: String
) {
    val tuuchoEngine = SystemCoreModules
        .remember(applicationModules)
        .koin
        .get<TuuchoEngineProtocol>()
    LaunchedEffect(Unit) {
        tuuchoEngine.start(url = onStartUrl)
    }
    tuuchoEngine.display()
}
