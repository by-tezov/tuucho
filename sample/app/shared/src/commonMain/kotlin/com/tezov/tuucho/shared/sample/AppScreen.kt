package com.tezov.tuucho.shared.sample

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.barrel.TuuchoEngineStart
import com.tezov.tuucho.core.domain.business.di.KoinMass
import com.tezov.tuucho.shared.sample.di.SystemSharedModules

@Composable
fun AppScreen(
    applicationModules: List<KoinMass>,
) {
    TuuchoEngineStart(
        koinMassModules = SystemSharedModules.invoke() + applicationModules,
        koinExtension = null,
        onStartUrl = "lobby/page-login"
    )

}
