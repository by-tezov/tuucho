package com.tezov.tuucho.core.barrel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.tezov.tuucho.core.barrel.di.SystemCoreModules
import com.tezov.tuucho.core.barrel.navigation.TuuchoBackHandler
import com.tezov.tuucho.core.domain.business.di.KoinContext
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.core.presentation.ui.renderer.TuuchoEngineProtocol
import org.koin.core.Koin

val localTuuchoKoin: ProvidableCompositionLocal<Koin> = staticCompositionLocalOf {
    KoinContext.koin
}

@Composable
fun TuuchoEngineStart(
    applicationModules: List<ModuleProtocol>,
    onStartUrl: String
) {
    val tuuchoKoin = SystemCoreModules
        .remember(applicationModules)
        .koin

    TuuchoBackHandler(tuuchoKoin)

    val tuuchoEngine = tuuchoKoin.get<TuuchoEngineProtocol>()
    LaunchedEffect(Unit) {
        tuuchoEngine.start(url = onStartUrl)
    }
    tuuchoEngine.display()
}
