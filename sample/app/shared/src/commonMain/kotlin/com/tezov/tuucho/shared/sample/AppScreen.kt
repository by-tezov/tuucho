package com.tezov.tuucho.shared.sample

import androidx.compose.runtime.Composable
import com.tezov.tuucho.core.barrel.TuuchoEngineStart
import com.tezov.tuucho.shared.sample.di.SystemSharedModules
import com.tezov.tuucho.core.domain.business.di.Koin.Companion.module
import com.tezov.tuucho.core.domain.business.di.Koin

@Composable
fun AppScreen(
    applicationModules: List<Koin>,
) {
    TuuchoEngineStart(
        koinModules = SystemSharedModules.invoke() + applicationModules,
        koinExtension = null,
        onStartUrl = "lobby/page-login"
    )

}
