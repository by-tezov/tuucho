package com.tezov.tuucho.core.presentation.ui.di

import com.tezov.tuucho.core.domain.business.di.Koin
import com.tezov.tuucho.core.domain.tool.annotation.TuuchoInternalApi

@TuuchoInternalApi
object SystemCoreUiModules {
    fun invoke(): List<Koin> = listOf(
        TuuchoEngineModule.invoke(),
        ViewModule.invoke(),
    )
}
