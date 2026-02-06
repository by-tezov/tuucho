package com.tezov.tuucho.core.barrel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import com.tezov.tuucho.core.barrel.di.SystemCoreModulesAndroid
import com.tezov.tuucho.core.barrel.navigation.TuuchoBackHandler
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass
import com.tezov.tuucho.core.presentation.ui._system.LocalTuuchoKoin
import com.tezov.tuucho.core.presentation.ui.render.TuuchoEngineProtocol
import org.koin.core.KoinApplication

@Composable
fun TuuchoEngineStart(
    koinMassModules: List<KoinMass>,
    koinExtension: (KoinApplication.() -> Unit)? = null,
    onStartUrl: String
) {
    val tuuchoKoin = SystemCoreModulesAndroid
        .remember(koinMassModules, koinExtension)
        .koin
    CompositionLocalProvider(
        LocalTuuchoKoin provides tuuchoKoin
    ) {
        TuuchoBackHandler(tuuchoKoin)
        val tuuchoEngine = tuuchoKoin.get<TuuchoEngineProtocol>()
        LaunchedEffect(Unit) {
            tuuchoEngine.start(url = onStartUrl)
        }
        tuuchoEngine.display()
    }
}
