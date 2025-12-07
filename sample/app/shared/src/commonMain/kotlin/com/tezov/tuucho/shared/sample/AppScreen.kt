package com.tezov.tuucho.shared.sample

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.barrel.TuuchoEngineStart
import com.tezov.tuucho.core.domain.business.protocol.ModuleProtocol
import com.tezov.tuucho.shared.sample.di.SystemSharedModules

@Composable
fun AppScreen(
    applicationModules: List<ModuleProtocol>,
) {
    TuuchoEngineStart(
        koinModules = SystemSharedModules.invoke() + applicationModules,
        koinExtension = null,
        onStartUrl = "lobby/page-login"
    )

}
