package com.tezov.tuucho.core.barrel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.tezov.tuucho.core.barrel.di.SystemCoreModules
import com.tezov.tuucho.core.barrel.navigation.TuuchoBackHandler
import com.tezov.tuucho.core.presentation.ui.render.TuuchoEngineProtocol
import org.koin.core.KoinApplication

@Composable
fun TuuchoEngineStart(
    koinModules: List<com.tezov.tuucho.core.domain.business.di.Koin>,
    koinExtension: (KoinApplication.() -> Unit)? = null,
    onStartUrl: String
) {
    val tuuchoKoin = SystemCoreModules
        .remember(koinModules, koinExtension)
        .koin

    TuuchoBackHandler(tuuchoKoin)

    val tuuchoEngine = tuuchoKoin.get<TuuchoEngineProtocol>()
    LaunchedEffect(Unit) {
        tuuchoEngine.start(url = onStartUrl)
    }
    tuuchoEngine.display()
}
