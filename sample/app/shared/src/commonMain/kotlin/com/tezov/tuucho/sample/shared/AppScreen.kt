package com.tezov.tuucho.sample.shared

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.barrel.TuuchoEngineStart
import com.tezov.tuucho.core.domain.business._system.koin.KoinMass
import com.tezov.tuucho.sample.shared.di.SystemSharedModules
import com.tezov.tuucho.sample.uiExtension.di.SystemUiExtensionModules
import org.koin.core.KoinApplication

@Composable
fun AppScreen(
    applicationModules: List<KoinMass>,
    koinExtension: (KoinApplication.() -> Unit)? = null,
) {
    TuuchoEngineStart(
        koinMassModules = SystemUiExtensionModules.invoke() +
                SystemSharedModules.invoke() +
                applicationModules,
        koinExtension = koinExtension,
        onStartUrl = "lobby/page-login"
    )
}
