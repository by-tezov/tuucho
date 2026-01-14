package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.di.KoinMass
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

@TuuchoInternalApi
object SystemCoreUiModules {
    fun invoke(): List<KoinMass> = listOf(
        TuuchoEngineModule.invoke(),
    )
}
